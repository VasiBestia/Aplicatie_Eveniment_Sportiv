package primary.Eveniment_Sportiv.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Locatii")
public class Locatie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Locatii")
    private Long idLocatie;

    @Column(name = "Nume_Locatii", nullable = false)
    private String numeLocatie;

    @Column(name = "Detalii_Locatie")
    private String detaliiLocatie;



    public Long getIdLocatie() {
        return idLocatie;
    }

    public void setIdLocatie(Long idLocatie) {
        this.idLocatie = idLocatie;
    }

    public String getNumeLocatie() {
        return numeLocatie;
    }

    public void setNumeLocatie(String numeLocatie) {
        this.numeLocatie = numeLocatie;
    }

    public String getDetaliiLocatie() {
        return detaliiLocatie;
    }

    public void setDetaliiLocatie(String detaliiLocatie) {
        this.detaliiLocatie = detaliiLocatie;
    }
}
