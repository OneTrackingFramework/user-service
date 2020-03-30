/**
 *
 */
package one.tracking.framework.repo;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import one.tracking.framework.entity.User;

/**
 * @author Marko Voß
 *
 */
public interface UserRepository extends CrudRepository<User, String> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUserName(String username);

  void deleteByEmailAndEmailVerificationStatus(String email, boolean status);

}
