/** Entitate De tip Tabela Pt Gestionarea Participantilor din cadrul Evenimentului.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */

package primary.Eveniment_Sportiv.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Participanti")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Participanti")
    private Long idParticipant;

    @Column(name = "Nume_Participant")
    private String nume;

    @Column(name = "Prenume_Participant")
    private String prenume;

    @Column(name = "Facultate")
    private String facultate;

    @Column(name = "An_Studiu")
    private Integer anStudiu;


    @OneToOne
    @JoinColumn(name = "Id_user", referencedColumnName = "Id_user")
    private UserAccount userAccount;


    public Long getIdParticipant() { return idParticipant; }
    public void setIdParticipant(Long idParticipant) { this.idParticipant = idParticipant; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }

    public String getFacultate() { return facultate; }
    public void setFacultate(String facultate) { this.facultate = facultate; }

    public Integer getAnStudiu() { return anStudiu; }
    public void setAnStudiu(Integer anStudiu) { this.anStudiu = anStudiu; }

    public UserAccount getUserAccount() { return userAccount; }
    public void setUserAccount(UserAccount userAccount) { this.userAccount = userAccount; }

    @ManyToMany
    @JoinTable(
            name = "Membri_Echipe",
            joinColumns = @JoinColumn(name = "Id_Participant"),
            inverseJoinColumns = @JoinColumn(name = "Id_Echipa")
    )
    private List<Team> echipe;


    public List<Team> getEchipe() {
        return echipe != null ? echipe : new ArrayList<>();
    }

}