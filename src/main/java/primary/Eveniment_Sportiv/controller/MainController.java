/**Controller pentru pagina principală a aplicației de evenimente sportive.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */


package primary.Eveniment_Sportiv.controller;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import primary.Eveniment_Sportiv.model.Meci;
import primary.Eveniment_Sportiv.model.Participant;
import primary.Eveniment_Sportiv.model.Team;
import primary.Eveniment_Sportiv.model.UserAccount;
import primary.Eveniment_Sportiv.repository.ParticipantRepository;
import primary.Eveniment_Sportiv.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.security.Principal;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @GetMapping("/index")
    public String showIndexPage(Model model, Principal principal, HttpSession session) {

        // 1. Dacă nu ești logat deloc (nici local, nici social), la revedere.
        if (principal == null) {
            return "redirect:/login";
        }

        UserAccount user = null;

        // 2. VERIFICĂM DACĂ AVEM DEJA SESIUNE (Cazul Google / Facebook)
        // Handlerele tale (GoogleLoginSuccessHandler) au pus deja user_id în sesiune.
        Long userIdDinSesiune = (Long) session.getAttribute("user_id");

        if (userIdDinSesiune != null) {
            // Suntem pe cazul GOOGLE/FACEBOOK -> Luăm userul direct după ID
            user = userRepository.findById(userIdDinSesiune).orElse(null);
        }
        else {
            // 3. NU AVEM SESIUNE (Cazul Local Login)
            // Aici "principal.getName()" este sigur email-ul (că așa l-am setat în CustomUserDetailsService)
            String email = principal.getName();
            user = userRepository.findByEmail(email).orElse(null);

            // Dacă l-am găsit, îi refacem sesiunea manual ca să nu mai avem probleme pe viitor
            if (user != null) {
                session.setAttribute("user_id", user.getIdUser());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("profile_pic", (user.getImagePath() != null) ? user.getImagePath() : "/img/undraw_profile.svg");
            }
        }

        // Siguranță: Dacă după toate astea userul e null, înapoi la login
        if (user == null) {
            return "redirect:/login";
        }

        // --- DE AICI ÎN JOS E CODUL TĂU VECHI PENTRU NOTIFICĂRI (NESCHIMBAT) ---

        Optional<Participant> partOpt = participantRepository.findByUserAccount(user);
        List<String> notificari = new ArrayList<>();
        int notificariCount = 0;

        if (partOpt.isPresent()) {
            Participant participant = partOpt.get();
            LocalDate azi = LocalDate.now();

            for (Team echipa : participant.getEchipe()) {
                for (Meci meci : echipa.getMeciuri()) {
                    if (meci.getDataMeci() != null && meci.getDataMeci().toLocalDate().equals(azi)) {
                        String ora = meci.getDataMeci().toLocalTime().toString();
                        String adversar = (meci.getEchipa1().equals(echipa)) ? meci.getEchipa2().getNumeEchipa() : meci.getEchipa1().getNumeEchipa();

                        notificari.add("🔥 MECI AZI! Joci împotriva " + adversar + " la ora " + ora);
                        notificariCount++;
                    }
                }
            }
        }

        model.addAttribute("listaNotificari", notificari);
        model.addAttribute("nrNotificari", notificariCount);

        // Luăm username-ul corect (fie din sesiune, fie din obiectul user)
        String displayUsername = (String) session.getAttribute("username");
        if (displayUsername == null) displayUsername = user.getUsername();
        model.addAttribute("username", displayUsername);

        return "index";
    }
}