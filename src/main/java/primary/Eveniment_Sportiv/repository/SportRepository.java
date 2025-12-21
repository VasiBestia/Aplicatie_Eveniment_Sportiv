package primary.Eveniment_Sportiv.repository;

import primary.Eveniment_Sportiv.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportRepository extends JpaRepository<Sport, Long> {
}