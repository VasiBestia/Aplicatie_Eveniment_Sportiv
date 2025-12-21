package primary.Eveniment_Sportiv.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mesaj;
    private LocalDateTime dataCreare;
    private boolean citit = false;
    private String tip;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;


    public Notification() {}

    public Notification(String mesaj, UserAccount user, String tip) {
        this.mesaj = mesaj;
        this.user = user;
        this.tip = tip;
        this.dataCreare = LocalDateTime.now();
    }

    public String getMesaj() { return mesaj; }
    public LocalDateTime getDataCreare() { return dataCreare; }
}