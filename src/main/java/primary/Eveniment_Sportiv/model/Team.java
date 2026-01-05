/** Entitate De tip Tabela Pt Gestionarea Echipelor din cadrul Evenimentului.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */

package primary.Eveniment_Sportiv.model;

import java.util.*;

import jakarta.persistence.*;

@Entity
@Table(name = "Echipe")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_echipa")
    private Long idEchipa;

    @Column(name = "Nume_Echipa", nullable = false, unique = true)
    private String numeEchipa;

    @Column(name = "Cod_Inscriere", unique = true)
    private String codInscriere;

    @Column(name = "Is_Eliminated")
    private boolean eliminated = false;


    @ManyToOne
    @JoinColumn(name = "Id_Sport", nullable = false)
    private Sport sport;


    @ManyToOne
    @JoinColumn(name = "Id_Capitan")
    private Participant capitan;


    @ManyToMany
    @JoinTable(
            name = "Membri_Echipe",
            joinColumns = @JoinColumn(name = "Id_Echipa"),
            inverseJoinColumns = @JoinColumn(name = "Id_Participant")
    )
    private Set<Participant> membri = new HashSet<>();


    public void addMembru(Participant participant) {
        this.membri.add(participant);
    }

    public Team() {
        this.codInscriere = UUID.randomUUID().toString();
    }

    public String getCodInscriere() { return codInscriere; }
    public void setCodInscriere(String codInscriere) { this.codInscriere = codInscriere; }

    public Long getIdEchipa() { return idEchipa; }
    public void setIdEchipa(Long idEchipa) { this.idEchipa = idEchipa; }

    public String getNumeEchipa() { return numeEchipa; }
    public void setNumeEchipa(String numeEchipa) { this.numeEchipa = numeEchipa; }

    public Sport getSport() { return sport; }
    public void setSport(Sport sport) { this.sport = sport; }

    public Participant getCapitan() { return capitan; }
    public void setCapitan(Participant capitan) { this.capitan = capitan; }

    public boolean isEliminated() { return eliminated; }
    public void setEliminated(boolean eliminated) { this.eliminated = eliminated; }

    public Set<Participant> getMembri() { return membri; }
    public void setMembri(Set<Participant> membri) { this.membri = membri; }

    @OneToMany(mappedBy = "echipaAcasa")
    private List<Meci> meciuriAcasa;


    @OneToMany(mappedBy = "echipaDeplasare")
    private List<Meci> meciuriDeplasare;


    public List<Meci> getMeciuri() {
        List<Meci> toateMeciurile = new ArrayList<>();
        if (meciuriAcasa != null) toateMeciurile.addAll(meciuriAcasa);
        if (meciuriDeplasare != null) toateMeciurile.addAll(meciuriDeplasare);
        return toateMeciurile;
    }

}
