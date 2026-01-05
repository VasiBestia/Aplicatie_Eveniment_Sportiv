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

        // Facebook trimite de obicei: "email", "name", "id"
        String email = facebookUser.getAttribute("email");
        String name = facebookUser.getAttribute("name");

        // Daca Facebook nu da email (se intampla rar, daca userul nu are), folosim ID-ul
        if (email == null) {
            email = facebookUser.getAttribute("id") + "@facebook.com";
        }

        System.out.println("LOG FACEBOOK: User logat: " + name + " | Email: " + email);

        // 2. Verificam in baza de date
        Optional<UserAccount> existingUser = userRepository.findByEmail(email);
        UserAccount user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Actualizam numele daca s-a schimbat pe Facebook
            if (name != null && !name.equals(user.getUsername())) {
                user.setUsername(name);
                userRepository.save(user);
            }
        } else {
            // 3. Cream utilizator nou
            user = new UserAccount();
            user.setEmail(email);
            user.setUsername(name);
            user.setImagePath("/img/undraw_profile.svg"); // Sau poti extrage poza de la FB daca vrei

            // Generam o parola random (userul nu o va sti, se logheaza doar cu FB)
            String randomPassword = UUID.randomUUID().toString();
            user.setParola(passwordEncoder.encode(randomPassword));

            userRepository.save(user);
        }

        // 4. Setam sesiunea
        HttpSession session = request.getSession();
        session.setAttribute("logged_in", true);
        session.setAttribute("user_id", user.getIdUser()); // Verifică dacă getter-ul e getId() sau getIdUser()
        session.setAttribute("username", user.getUsername());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("profile_pic", user.getImagePath());

        // 5. Redirect acasa
        response.sendRedirect("/index");
    }
}