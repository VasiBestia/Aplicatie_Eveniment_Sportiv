package primary.Eveniment_Sportiv.repository;
import primary.Eveniment_Sportiv.model.Meci;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MeciRepository extends JpaRepository<Meci, Long> {

    List<Meci> findAllByOrderByDataOraAsc();

    List<Meci> findAllBySport_NumeSportOrderByDataOraAsc(String numeSport);
}