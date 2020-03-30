/**
 *
 */
package one.tracking.framework.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import one.tracking.framework.entity.User;
import one.tracking.framework.repo.UserRepository;
import one.tracking.framework.util.JWTHelper;

/**
 * @author Marko Voß
 *
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JWTHelper jwtHelper;

  private final PasswordEncoder passwordEncoder;

  public CustomAuthenticationProvider(final PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

    final String email = authentication.getName();
    final String password = authentication.getCredentials().toString();

    final Optional<User> userOp = this.userRepository.findByEmail(email);
    if (userOp.isEmpty() || !this.passwordEncoder.matches(password, userOp.get().getEncryptedPassword()))
      throw new BadCredentialsException("Invalid credentials");

    // Create qr code
    final User user = userOp.get();
    user.setQrCode(generateQrCode(user));
    this.userRepository.save(user);

    final List<GrantedAuthority> authorities = new ArrayList<>();

    return new UsernamePasswordAuthenticationToken(user.getId(), password, authorities);
  }

  /**
   *
   * @param user
   * @return
   */
  private String generateQrCode(final User user) {
    return this.jwtHelper.createJWT(user.getId(), SecurityConstants.DEFAULT_EXPIRATION);
  }

  @Override
  public boolean supports(final Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
