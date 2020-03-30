/**
 *
 */
package one.tracking.framework.web;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.Claims;
import one.tracking.framework.dto.UserAddressDto;
import one.tracking.framework.dto.UserCreateDto;
import one.tracking.framework.dto.UserDto;
import one.tracking.framework.entity.User;
import one.tracking.framework.entity.UserAddress;
import one.tracking.framework.events.UserUpdated;
import one.tracking.framework.producers.UserUpdatedProducer;
import one.tracking.framework.repo.UserAddressRepository;
import one.tracking.framework.repo.UserRepository;
import one.tracking.framework.security.SecurityConstants;
import one.tracking.framework.util.JWTHelper;

/**
 * TODO: Restrict edit methods to verified {@link User}s only.
 *
 * @author Marko Voß
 *
 */
@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserAddressRepository addressRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JWTHelper jwtHelper;

  @Autowired
  private UserUpdatedProducer userUpdatedProducer;

  @RequestMapping(method = RequestMethod.GET)
  public UserDto getUser(final Authentication authentication) {

    return UserDto.fromEntity(loadUser(authentication));
  }

  @RequestMapping(method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
  public String createUser(
      @RequestBody
      final UserCreateDto userCreate) {

    if (this.userRepository.existsByEmail(userCreate.getEmail()))
      throw new IllegalArgumentException("Email does already exist.");

    if (this.userRepository.existsByUserName(userCreate.getUserName()))
      throw new IllegalArgumentException("Username does already exist.");

    final String verificationJwt =
        this.jwtHelper.createJWT(userCreate.getEmail(), SecurityConstants.DEFAULT_EXPIRATION);

    final User user = this.userRepository.save(User.builder()
        .email(userCreate.getEmail())
        .emailVerificationToken(verificationJwt)
        .encryptedPassword(this.passwordEncoder.encode(userCreate.getPassword()))
        .firstName(userCreate.getFirstName())
        .lastName(userCreate.getLastName())
        .userName(userCreate.getUserName())
        .build());

    // TODO: Email verification: Send email

    this.userUpdatedProducer.userUpdated(UserUpdated.builder()
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .id(user.getId())
        .lastName(user.getLastName())
        .password(user.getEncryptedPassword())
        .build());

    return user.getId();
  }

  @RequestMapping(method = RequestMethod.POST, path = "/verify")
  public void verifyEmail(@RequestBody
  final String verificationJwt) {

    final Claims claims = this.jwtHelper.decodeJWT(verificationJwt);
    final String email = claims.getSubject();
    final Date expiration = claims.getExpiration();

    if (expiration.after(new Date())) {
      /*
       * Delete account to enable new registration using the same unique fields if and only if account has
       * not yet been enabled.
       */
      this.userRepository.deleteByEmailAndEmailVerificationStatus(email, false);
      return; // ignore invalid requests
    }

    final Optional<User> userOp = this.userRepository.findByEmail(email);

    if (userOp.isEmpty())
      return; // ignore invalid requests

    final User user = userOp.get();
    user.setEmailVerificationStatus(true);
    this.userRepository.save(user);

  }

  @RequestMapping(method = RequestMethod.GET, path = "/qr-code")
  public String getQrCode(final Authentication authentication) {
    return loadUser(authentication).getQrCode();
  }

  @RequestMapping(method = RequestMethod.POST, path = "/address")
  public void postAddress(
      @RequestBody
      final UserAddressDto address,
      final Authentication authentication) {

    final User user = loadUser(authentication);

    final Optional<UserAddress> existingAddressOp =
        this.addressRepository.findByUserIdAndType(user.getId(), address.getType());

    if (existingAddressOp.isPresent()) {
      // update

      final UserAddress existingAddress = existingAddressOp.get();
      existingAddress.setCity(address.getCity());
      existingAddress.setCountry(address.getCountry());
      existingAddress.setPostalCode(address.getPostalCode());
      existingAddress.setStreetname(address.getStreetname());
      existingAddress.setUserUpdate(user);
      existingAddress.setUserUpdate(user);

      this.addressRepository.save(existingAddress);

    } else {
      // create

      this.addressRepository.save(UserAddress.builder()
          .addressExternalId(UUID.randomUUID().toString())
          .city(address.getCity())
          .country(address.getCountry())
          .postalCode(address.getPostalCode())
          .streetname(address.getStreetname())
          .type(address.getType())
          .user(user)
          .userCreate(user)
          .build());

    }
  }

  @RequestMapping(method = RequestMethod.GET, path = "/address")
  public List<UserAddressDto> getAddresses(final Authentication authentication) {

    final User user = loadUser(authentication);

    return this.addressRepository.findByUserId(user.getId()).stream().map(UserAddressDto::fromEntity)
        .collect(Collectors.toList());
  }

  @RequestMapping(method = RequestMethod.DELETE, path = "/address/{type}")
  @Transactional
  public void deleteAddress(
      @PathVariable("type")
      final String type) {

    this.addressRepository.deleteByType(type);
  }

  /**
   *
   * @param authentication
   * @return
   */
  private User loadUser(final Authentication authentication) {
    return this.userRepository.findById(authentication.getName()).get();
  }
}
