/** Controller pentru gestionarea cererilor legate de meciuri sportive.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */

package primary.Eveniment_Sportiv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import primary.Eveniment_Sportiv.model.Meci;
import primary.Eveniment_Sportiv.repository.MeciRepository;
import java.util.List;
import java.util.Optional;

@Controller
public class MeciController {

    @Autowired
    private MeciRepository meciRepository;


    @GetMapping("/meciuri")
    public String showMatchesPage(
            @RequestParam(name = "sport", required = false) String sport,
            Model model
    ) {
        List<Meci> meciuri;

        if (sport != null && !sport.isEmpty()) {

            meciuri = meciRepository.findAllBySport_NumeSportOrderByDataOraAsc(sport);
            model.addAttribute("activeSport", sport);
        } else {

            meciuri = meciRepository.findAllByOrderByDataOraAsc();
            model.addAttribute("activeSport", "Toate");
        }

        model.addAttribute("meciuri", meciuri);
        return "meciuri";
    }


    @GetMapping("/meci/{id}")
    public String showMatchDetails(@PathVariable("id") Long id, Model model) {
        Optional<Meci> meciOpt = meciRepository.findById(id);

        if (meciOpt.isEmpty()) {
            return "redirect:/meciuri";
        }

        model.addAttribute("meci", meciOpt.get());
        return "meci_detalii";
    }
}
