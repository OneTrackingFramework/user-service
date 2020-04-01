/**
 *
 */
package one.tracking.framework.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import one.tracking.framework.dto.UserAddressDto;
import one.tracking.framework.dto.UserCreateDto;
import one.tracking.framework.repo.UserRepository;

/**
 * @author Marko Voß
 *
 */
@Service
public class TestDataService {

  private static final Logger LOG = LoggerFactory.getLogger(TestDataService.class);

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @EventListener
  public void onApplicationEvent(final ApplicationStartedEvent event) throws Exception {
    createExampleData();
  }

  private final void createExampleData() throws Exception {

    /*
     * User A
     */

    final String userA = this.userService.createUser(UserCreateDto.builder()
        .userName("userA")
        .email("user.a@example.com")
        .password("userA")
        .firstName("User")
        .lastName("A")
        .build());

    this.userService.createAddress(userA, UserAddressDto.builder()
        .city("Berlin")
        .country("Germany")
        .postalCode("10178")
        .streetname("Alexanderplatz 1")
        .type("HOME")
        .build());

    this.userService.createAddress(userA, UserAddressDto.builder()
        .city("Berlin")
        .country("Germany")
        .postalCode("10178")
        .streetname("Sophienstraße 23")
        .type("WORK")
        .build());

    /*
     * User B
     */

    final String userB = this.userService.createUser(UserCreateDto.builder()
        .userName("userB")
        .email("user.b@example.com")
        .password("userB")
        .firstName("User")
        .lastName("B")
        .build());

    this.userService.createAddress(userB, UserAddressDto.builder()
        .city("Berlin")
        .country("Germany")
        .postalCode("10407")
        .streetname("Syringenweg 20")
        .type("HOME")
        .build());

    this.userService.createAddress(userB, UserAddressDto.builder()
        .city("Berlin")
        .country("Germany")
        .postalCode("10407")
        .streetname("Prenzlauer Berg")
        .type("WORK")
        .build());

    /*
     * User C
     */

    final String userC = this.userService.createUser(UserCreateDto.builder()
        .userName("userC")
        .email("user.c@example.com")
        .password("userC")
        .firstName("User")
        .lastName("C")
        .build());

    this.userService.createAddress(userC, UserAddressDto.builder()
        .city("Berlin")
        .country("Germany")
        .postalCode("10437")
        .streetname("Stubbenkammerstraße 10")
        .type("HOME")
        .build());

    this.userService.createAddress(userC, UserAddressDto.builder()
        .city("Berlin")
        .country("Germany")
        .postalCode("10409")
        .streetname("Storkower Str. 15")
        .type("WORK")
        .build());

    /*
     * User D
     */

    final String userD = this.userService.createUser(UserCreateDto.builder()
        .userName("userD")
        .email("user.d@example.com")
        .password("userD")
        .firstName("User")
        .lastName("D")
        .build());

    this.userService.createAddress(userD, UserAddressDto.builder()
        .city("Berlin")
        .country("Germany")
        .postalCode("10437")
        .streetname("Stubbenkammerstraße 10")
        .type("HOME")
        .build());

    this.userService.createAddress(userD, UserAddressDto.builder()
        .city("Berlin")
        .country("Germany")
        .postalCode("10409")
        .streetname("Storkower Str. 15")
        .type("WORK")
        .build());

    this.userService.verifyEmail(this.userRepository.findById(userA).get().getEmailVerificationToken());
    this.userService.verifyEmail(this.userRepository.findById(userB).get().getEmailVerificationToken());
    this.userService.verifyEmail(this.userRepository.findById(userC).get().getEmailVerificationToken());
    this.userService.verifyEmail(this.userRepository.findById(userD).get().getEmailVerificationToken());

    LOG.info("Created UserA: {}", userA);
    LOG.info("Created UserB: {}", userB);
    LOG.info("Created UserC: {}", userC);
    LOG.info("Created UserD: {}", userD);
  }
}
