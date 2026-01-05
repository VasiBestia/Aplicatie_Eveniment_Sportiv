/** Entitate De tip Tabela Pt Gestionarea Sporturilor din cadrul Evenimentului.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */

package primary.Eveniment_Sportiv.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Sporturi")
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Sport")
    private Long idSport;

    @Column(name = "Nume_Sport", nullable = false)
    private String numeSport;

    @Column(name = "Team_Size")
    private Integer teamSize;


    public Long getIdSport() { return idSport; }
    public void setIdSport(Long idSport) { this.idSport = idSport; }

    public String getNumeSport() { return numeSport; }
    public void setNumeSport(String numeSport) { this.numeSport = numeSport; }

    public Integer getTeamSize() { return teamSize; }
    public void setTeamSize(Integer teamSize) { this.teamSize = teamSize; }
}