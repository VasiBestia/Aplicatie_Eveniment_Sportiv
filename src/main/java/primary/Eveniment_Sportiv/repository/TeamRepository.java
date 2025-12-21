package primary.Eveniment_Sportiv.repository;

import primary.Eveniment_Sportiv.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import primary.Eveniment_Sportiv.model.Participant;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByNumeEchipa(String numeEchipa);

    Optional<Team> findByCodInscriere(String codInscriere);


    List<Team> findAllByCapitan(Participant capitan);


    boolean existsByCapitanAndSportIdSport(Participant capitan, Long sportId);

    List<Team> findByEliminatedFalse();
}