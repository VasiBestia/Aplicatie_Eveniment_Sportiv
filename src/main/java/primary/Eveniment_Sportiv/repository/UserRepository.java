package primary.Eveniment_Sportiv.repository;

import primary.Eveniment_Sportiv.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}