/** Entitate De tip Tabela Pt Gestionarea Meciurilor din cadrul Evenimentului.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */


package primary.Eveniment_Sportiv.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Meciuri")
public class Meci {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Meci")
    private Long idMeci;

    @ManyToOne
    @JoinColumn(name = "Id_Sport")
    private Sport sport;


    @ManyToOne
    @JoinColumn(name = "Id_Echipa_Acasa")
    private Team echipaAcasa;

    @ManyToOne
    @JoinColumn(name = "Id_Echipa_Deplasare")
    private Team echipaDeplasare;

    @ManyToOne
    @JoinColumn(name = "Id_Locatie")
    private Locatie locatie;

    @Column(name = "Start_Time")
    private LocalDateTime dataOra;


    @Column(name = "Status")
    private String status;

    @Column(name = "Scor_Acasa")
    private Integer scorAcasa;

    @Column(name = "Scor_Deplasare")
    private Integer scorDeplasare;


    public String getOraFormatata() {
        return dataOra.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getDataFormatata() {
        return dataOra.format(DateTimeFormatter.ofPattern("dd MMM"));
    }

    public LocalDateTime getDataMeci() {
        return dataOra;
    }

    public Team getEchipa1() {
        return echipaAcasa;
    }

    public Team getEchipa2() {
        return echipaDeplasare;
    }


    public Long getIdMeci() { return idMeci; }
    public void setIdMeci(Long idMeci) { this.idMeci = idMeci; }
    public Sport getSport() { return sport; }
    public void setSport(Sport sport) { this.sport = sport; }
    public Team getEchipaAcasa() { return echipaAcasa; }
    public void setEchipaAcasa(Team echipaAcasa) { this.echipaAcasa = echipaAcasa; }
    public Team getEchipaDeplasare() { return echipaDeplasare; }
    public void setEchipaDeplasare(Team echipaDeplasare) { this.echipaDeplasare = echipaDeplasare; }
    public Locatie getLocatie() { return locatie; }
    public void setLocatie(Locatie locatie) { this.locatie = locatie; }
    public LocalDateTime getDataOra() {return dataOra;}
    public void setDataOra(LocalDateTime dataOra) {this.dataOra = dataOra;}
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getScorAcasa() { return scorAcasa; }
    public void setScorAcasa(Integer scorAcasa) { this.scorAcasa = scorAcasa; }
    public Integer getScorDeplasare() { return scorDeplasare; }
    public void setScorDeplasare(Integer scorDeplasare) { this.scorDeplasare = scorDeplasare; }

}