/**
 *
 */
package one.tracking.framework.repo;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import one.tracking.framework.config.TestConfig;
import one.tracking.framework.entity.User;

/**
 * @author Marko Voß
 *
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TestConfig.class)
@EnableKafka
@EmbeddedKafka(
    partitions = 1,
    controlledShutdown = false,
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:3333",
        "port=3333"
    })
public class UserRepositoryTest {

  @Autowired
  private DataSource dataSource;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void injectedComponentsAreNotNull() {
    assertThat(this.dataSource).isNotNull();
    assertThat(this.jdbcTemplate).isNotNull();
    assertThat(this.entityManager).isNotNull();
    assertThat(this.userRepository).isNotNull();
    assertThat(this.passwordEncoder).isNotNull();
  }

  @Test
  void test_userCreation() {

    final User createdUser = this.userRepository.save(User.builder()
        .email("max.mustermann@example.com")
        .emailVerificationToken(UUID.randomUUID().toString())
        .encryptedPassword(this.passwordEncoder.encode("Hallo123!"))
        .firstName("Max")
        .lastName("Mustermann")
        .userName("@Max")
        .build());

    assertThat(this.userRepository.findById(createdUser.getId())).isNotNull();
    assertThat(this.userRepository.findByEmail(createdUser.getEmail())).isNotNull();
    assertThat(createdUser.getEmailVerificationToken()).isNotNull();
    assertThat(createdUser.getTimestampCreate()).isNotNull();
    assertThat(createdUser.getTimestampUpdate()).isNull();
  }
}

