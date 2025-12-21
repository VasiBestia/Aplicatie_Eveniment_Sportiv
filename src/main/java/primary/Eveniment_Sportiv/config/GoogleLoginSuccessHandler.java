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
public class GoogleLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    @Lazy
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();
        String email = googleUser.getAttribute("email");
        String name = googleUser.getAttribute("name");
        String picture = googleUser.getAttribute("picture");

        System.out.println("LOG GOOGLE: Nume = " + googleUser.getAttribute("name"));
        System.out.println("LOG GOOGLE: Link Poza = " + picture);

        Optional<UserAccount> existingUser = userRepository.findByEmail(email);
        UserAccount user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {

            user = new UserAccount();
            user.setEmail(email);
            user.setUsername(name);
            user.setImagePath(picture);

            String randomPassword = UUID.randomUUID().toString();
            user.setParola(passwordEncoder.encode(randomPassword));

            userRepository.save(user);
        }


        HttpSession session = request.getSession();
        session.setAttribute("logged_in", true);
        session.setAttribute("user_id", user.getIdUser());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("email", user.getEmail());

        String photoUrl = (user.getImagePath() != null && !user.getImagePath().isEmpty()) ? user.getImagePath() : picture;
        session.setAttribute("profile_pic", photoUrl);

        response.sendRedirect("/index");
    }
}