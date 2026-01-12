/** Clasa pentru gestionarea logicii de autentificare OAuth2 a utilizatorilor prin Facebook.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */

package primary.Eveniment_Sportiv.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import primary.Eveniment_Sportiv.model.UserAccount;
import primary.Eveniment_Sportiv.repository.UserRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class FacebookLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 1. Preluam datele de la Facebook
        OAuth2User facebookUser = (OAuth2User) authentication.getPrincipal();

        // Extragem ID-ul (folosit pentru poza)
        String facebookId = facebookUser.getAttribute("id");

        // Extragem Email si Nume
        String email = facebookUser.getAttribute("email");
        String name = facebookUser.getAttribute("name");

        String photoUrl = "/images/default-avatar.png";

        Map<String, Object> picture = facebookUser.getAttribute("picture");
        if (picture != null) {
            Map<String, Object> data = (Map<String, Object>) picture.get("data");
            if (data != null && data.get("url") != null) {
                photoUrl = data.get("url").toString();
            }
        }

        // Fallback pentru email (daca totusi e null, macar sa stim)
        if (email == null) {
            System.out.println("ATENTIE: Facebook nu a returnat email. Folosim ID-ul.");
            email = facebookId + "@facebook.com";
        }

        System.out.println("LOG FACEBOOK: User: " + name + " | Email: " + email + " | Poza: " + photoUrl);

        // 2. Verificam in baza de date
        Optional<UserAccount> existingUser = userRepository.findByEmail(email);
        UserAccount user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Actualizam poza daca utilizatorul si-a schimbat-o pe Facebook
            user.setImagePath(photoUrl);
            // Actualizam numele daca e diferit
            if (name != null && !name.equals(user.getUsername())) {
                user.setUsername(name);
            }
            userRepository.save(user); // Salvam actualizarile
        } else {
            // 3. Cream utilizator nou
            user = new UserAccount();
            user.setEmail(email);
            user.setUsername(name);
            user.setImagePath(photoUrl); // <--- AICI SALVAM POZA REALA

            String randomPassword = UUID.randomUUID().toString();
            user.setParola(passwordEncoder.encode(randomPassword));

            userRepository.save(user);
        }

        // 4. Setam sesiunea
        HttpSession session = request.getSession();
        session.setAttribute("logged_in", true);
        session.setAttribute("user_id", user.getIdUser());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("profile_pic", user.getImagePath()); // Acum va fi link-ul de FB

        System.out.println("POZA FINALA = " + photoUrl);
        // 5. Redirect acasa
        response.sendRedirect("/index");
    }
}