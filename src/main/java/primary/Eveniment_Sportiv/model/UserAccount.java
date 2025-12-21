package primary.Eveniment_Sportiv.model;

import jakarta.persistence.*;

@Entity
@Table(name = "USER_ACCOUNT")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_user")
    private Long idUser;

    @Column(name = "Username", nullable = false, unique = true)
    private String username;

    @Column(name = "Parola", nullable = false)
    private String parola;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "Image_path")
    private String imagePath;

    public UserAccount() {}


    public Long getIdUser() { return idUser; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getParola() { return parola; }
    public void setParola(String parola) { this.parola = parola; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
