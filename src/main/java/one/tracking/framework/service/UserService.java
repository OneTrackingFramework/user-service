/**
 *
 */
package one.tracking.framework.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import one.tracking.framework.dto.UserAddressDto;
import one.tracking.framework.dto.UserCreateDto;
import one.tracking.framework.dto.UserDto;
import one.tracking.framework.entity.User;
import one.tracking.framework.entity.UserAddress;
import one.tracking.framework.kafka.events.UserCredentials;
import one.tracking.framework.kafka.producers.UserCredentialsProducer;
import one.tracking.framework.repo.UserAddressRepository;
import one.tracking.framework.repo.UserRepository;
import one.tracking.framework.security.SecurityConstants;
import one.tracking.framework.util.JWTHelper;

/**
 * @author Marko Voß
 *
 */
@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserAddressRepository addressRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JWTHelper jwtHelper;

  @Autowired
  private UserCredentialsProducer producer;

  @Autowired
  private ObjectMapper mapper;

  @Value("${app.jwe.secret}")
  private String jweEncodedSecret;

  public UserDto getUser(final String userId) {

    return UserDto.fromEntity(loadUser(userId));
  }

  public String createUser(final UserCreateDto userCreate) throws Exception {

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

    return user.getId();
  }

  public void verifyEmail(final String verificationJwt) throws Exception {

    final Claims claims = this.jwtHelper.decodeJWT(verificationJwt);
    final String email = claims.getSubject();
    final Date expiration = claims.getExpiration();

    if (new Date().after(expiration)) {
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

    /*
     * Send credentials event to auth-service to enable login.
     */

    final UserCredentials userCredentials = UserCredentials.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .encrytedPassword(user.getEncryptedPassword())
        .build();

    final String jwe =
        this.jwtHelper.createJWE(this.jweEncodedSecret, this.mapper.writeValueAsString(userCredentials));

    this.producer.send(user.getId(), jwe);
  }

  public String getQrCode(final String userId) {
    return loadUser(userId).getQrCode();
  }

  public void createAddress(final String userId, final UserAddressDto address) {

    final User user = loadUser(userId);

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

  public List<UserAddressDto> getAddresses(final String userId) {

    final User user = loadUser(userId);

    return this.addressRepository.findByUserId(user.getId()).stream().map(UserAddressDto::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional
  public void deleteAddress(final String type) {

    this.addressRepository.deleteByType(type);
  }

  /**
   *
   * @param authentication
   * @return
   */
  private User loadUser(final String userId) {
    return this.userRepository.findById(userId).get();
  }
}
