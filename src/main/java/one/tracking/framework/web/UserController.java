/**
 *
 */
package one.tracking.framework.web;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import one.tracking.framework.dto.UserAddressDto;
import one.tracking.framework.dto.UserCreateDto;
import one.tracking.framework.dto.UserDto;
import one.tracking.framework.entity.User;
import one.tracking.framework.service.UserService;

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
  private UserService userService;

  @RequestMapping(method = RequestMethod.GET)
  public UserDto getUser(final Authentication authentication) {

    return this.userService.getUser(authentication.getName());
  }

  @RequestMapping(method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
  public String createUser(
      @RequestBody
      @Valid
      final UserCreateDto userCreate) throws Exception {

    return this.userService.createUser(userCreate);
  }

  @RequestMapping(method = RequestMethod.POST, path = "/verify")
  public void verifyEmail(
      @RequestBody
      @NotBlank
      final String verificationJwt) throws Exception {

    this.userService.verifyEmail(verificationJwt);

  }

  @RequestMapping(method = RequestMethod.GET, path = "/qr-code")
  public String getQrCode(final Authentication authentication) {

    return this.userService.getQrCode(authentication.getName());
  }

  @RequestMapping(method = RequestMethod.POST, path = "/address")
  public void createAddress(
      @RequestBody
      @Valid
      final UserAddressDto address,
      final Authentication authentication) {

    this.userService.createAddress(authentication.getName(), address);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/address")
  public List<UserAddressDto> getAddresses(final Authentication authentication) {

    return this.userService.getAddresses(authentication.getName());
  }

  @RequestMapping(method = RequestMethod.DELETE, path = "/address/{type}")
  @Transactional
  public void deleteAddress(
      @PathVariable("type")
      @NotBlank
      final String type) {

    this.userService.deleteAddress(type);
  }

}
