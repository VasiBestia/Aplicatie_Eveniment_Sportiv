package primary.Eveniment_Sportiv.repository;

import primary.Eveniment_Sportiv.model.Participant;
import primary.Eveniment_Sportiv.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByUserAccount(UserAccount userAccount);
}