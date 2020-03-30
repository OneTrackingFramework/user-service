/**
 *
 */
package one.tracking.framework.service;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import one.tracking.framework.entity.User;
import one.tracking.framework.entity.UserAddress;
import one.tracking.framework.repo.UserAddressRepository;
import one.tracking.framework.repo.UserRepository;

/**
 * @author Marko Voß
 *
 */
@Service
@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
public class MainService {

  private static final Logger LOG = LoggerFactory.getLogger(MainService.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserAddressRepository addressRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ObjectMapper objectMapper;

  @EventListener
  public void onApplicationEvent(final ApplicationStartedEvent event) throws JsonProcessingException {
    createExampleData();
  }

  private final void createExampleData() throws JsonProcessingException {

    final ObjectWriter writer = this.objectMapper.writerWithDefaultPrettyPrinter();

    /*
     * User A
     */

    final User userA = this.userRepository.save(User.builder()
        .userName("userA")
        .email("user.a@example.com")
        .emailVerificationStatus(true)
        .emailVerificationToken("SYSTEM")
        .encryptedPassword(this.passwordEncoder.encode("userA"))
        .firstName("User")
        .lastName("A")
        .isEnabled(true)
        .build());

    final UserAddress addressA1 = this.addressRepository.save(UserAddress.builder()
        .addressExternalId(UUID.randomUUID().toString())
        .city("Berlin")
        .country("Germany")
        .postalCode("10178")
        .streetname("Alexanderplatz 1")
        .type("HOME")
        .user(userA)
        .userCreate(userA)
        .build());

    final UserAddress addressA2 = this.addressRepository.save(UserAddress.builder()
        .addressExternalId(UUID.randomUUID().toString())
        .city("Berlin")
        .country("Germany")
        .postalCode("10178")
        .streetname("Sophienstraße 23")
        .type("WORK")
        .user(userA)
        .userCreate(userA)
        .build());

    LOG.info("Created User A:\n{}", writer.writeValueAsString(userA));
    LOG.info("Created User A Address HOME:\n{}", writer.writeValueAsString(addressA1));
    LOG.info("Created User A Address WORK:\n{}", writer.writeValueAsString(addressA2));

    /*
     * User B
     */

    final User userB = this.userRepository.save(User.builder()
        .userName("userB")
        .email("user.b@example.com")
        .emailVerificationStatus(true)
        .emailVerificationToken("SYSTEM")
        .encryptedPassword(this.passwordEncoder.encode("userB"))
        .firstName("User")
        .lastName("B")
        .isEnabled(true)
        .build());

    final UserAddress addressB1 = this.addressRepository.save(UserAddress.builder()
        .addressExternalId(UUID.randomUUID().toString())
        .city("Berlin")
        .country("Germany")
        .postalCode("10407")
        .streetname("Syringenweg 20")
        .type("HOME")
        .user(userB)
        .userCreate(userB)
        .build());

    final UserAddress addressB2 = this.addressRepository.save(UserAddress.builder()
        .addressExternalId(UUID.randomUUID().toString())
        .city("Berlin")
        .country("Germany")
        .postalCode("10407")
        .streetname("Prenzlauer Berg")
        .type("WORK")
        .user(userB)
        .userCreate(userB)
        .build());

    LOG.info("Created User B:\n{}", writer.writeValueAsString(userB));
    LOG.info("Created User B Address HOME:\n{}", writer.writeValueAsString(addressB1));
    LOG.info("Created User B Address WORK:\n{}", writer.writeValueAsString(addressB2));

    /*
     * User C
     */

    final User userC = this.userRepository.save(User.builder()
        .userName("userC")
        .email("user.c@example.com")
        .emailVerificationStatus(true)
        .emailVerificationToken("SYSTEM")
        .encryptedPassword(this.passwordEncoder.encode("userC"))
        .firstName("User")
        .lastName("C")
        .isEnabled(true)
        .build());

    final UserAddress addressC1 = this.addressRepository.save(UserAddress.builder()
        .addressExternalId(UUID.randomUUID().toString())
        .city("Berlin")
        .country("Germany")
        .postalCode("10437")
        .streetname("Stubbenkammerstraße 10")
        .type("HOME")
        .user(userB)
        .userCreate(userB)
        .build());

    final UserAddress addressC2 = this.addressRepository.save(UserAddress.builder()
        .addressExternalId(UUID.randomUUID().toString())
        .city("Berlin")
        .country("Germany")
        .postalCode("10409")
        .streetname("Storkower Str. 15")
        .type("WORK")
        .user(userB)
        .userCreate(userB)
        .build());

    LOG.info("Created User C:\n{}", writer.writeValueAsString(userC));
    LOG.info("Created User C Address HOME:\n{}", writer.writeValueAsString(addressC1));
    LOG.info("Created User C Address WORK:\n{}", writer.writeValueAsString(addressC2));

    /*
     * User D
     */

    final User userD = this.userRepository.save(User.builder()
        .userName("userD")
        .email("user.d@example.com")
        .emailVerificationStatus(true)
        .emailVerificationToken("SYSTEM")
        .encryptedPassword(this.passwordEncoder.encode("userD"))
        .firstName("User")
        .lastName("D")
        .isEnabled(true)
        .build());

    final UserAddress addressD1 = this.addressRepository.save(UserAddress.builder()
        .addressExternalId(UUID.randomUUID().toString())
        .city("Berlin")
        .country("Germany")
        .postalCode("10437")
        .streetname("Stubbenkammerstraße 10")
        .type("HOME")
        .user(userB)
        .userCreate(userB)
        .build());

    final UserAddress addressD2 = this.addressRepository.save(UserAddress.builder()
        .addressExternalId(UUID.randomUUID().toString())
        .city("Berlin")
        .country("Germany")
        .postalCode("10409")
        .streetname("Storkower Str. 15")
        .type("WORK")
        .user(userB)
        .userCreate(userB)
        .build());

    LOG.info("Created User D:\n{}", writer.writeValueAsString(userD));
    LOG.info("Created User D Address HOME:\n{}", writer.writeValueAsString(addressD1));
    LOG.info("Created User D Address WORK:\n{}", writer.writeValueAsString(addressD2));

  }
}
