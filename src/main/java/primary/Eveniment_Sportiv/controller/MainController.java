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

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @GetMapping("/index")
    public String showIndexPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";


        Optional<UserAccount> userOpt = Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizator negăsit")));
        if (userOpt.isEmpty()) return "redirect:/login";
        UserAccount user = userOpt.get();

        Optional<Participant> partOpt = participantRepository.findByUserAccount(user);


        List<String> notificari = new ArrayList<>();
        int notificariCount = 0;

        if (partOpt.isPresent()) {
            Participant participant = partOpt.get();


            LocalDate azi = LocalDate.now();


            for (Team echipa : participant.getEchipe()) {

                for (Meci meci : echipa.getMeciuri()) {
                    if (meci.getDataMeci().toLocalDate().equals(azi)) {
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


        model.addAttribute("username", session.getAttribute("username"));
        return "index";
    }
}