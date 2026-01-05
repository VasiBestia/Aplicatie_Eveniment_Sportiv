/** Controller pentru administrarea meciurilor sportive, incluzând funcționalități de editare și adăugare meciuri.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */


package primary.Eveniment_Sportiv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import primary.Eveniment_Sportiv.model.Meci;
import primary.Eveniment_Sportiv.model.Team;
import primary.Eveniment_Sportiv.repository.LocatieRepository;
import primary.Eveniment_Sportiv.repository.MeciRepository;
import primary.Eveniment_Sportiv.repository.SportRepository;
import primary.Eveniment_Sportiv.repository.TeamRepository;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class MatchAdminController {

    @Autowired
    private MeciRepository matchRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private SportRepository sportRepository;
    @Autowired private LocatieRepository locationRepository;


    @GetMapping("/edit-meci/{id}")
    public String showEditMatchForm(@PathVariable("id") Long id, Model model) {
        Optional<Meci> meciOpt = matchRepository.findById(id);

        if (meciOpt.isEmpty()) {
            return "redirect:/meciuri";
        }

        model.addAttribute("meci", meciOpt.get());
        return "edit-meci";
    }

    @GetMapping("/adauga-meci")
    public String showAddMatchForm(Model model) {
        model.addAttribute("meci", new Meci());
        model.addAttribute("listaEchipe", teamRepository.findByEliminatedFalse());
        model.addAttribute("listaSporturi", sportRepository.findAll());
        model.addAttribute("listaLocatii", locationRepository.findAll());

        return "adauga-meci";
    }


    @PostMapping("/update-meci")
    public String updateMatchScore(
            @RequestParam("idMeci") Long idMeci,
            @RequestParam("scorAcasa") Integer scorAcasa,
            @RequestParam("scorDeplasare") Integer scorDeplasare,
            @RequestParam("status") String status,

            @RequestParam(value = "eliminaGazdele", required = false) boolean eliminaGazdele,
            @RequestParam(value = "eliminaOaspetii", required = false) boolean eliminaOaspetii
    ) {
        Optional<Meci> meciOpt = matchRepository.findById(idMeci);

        if (meciOpt.isPresent()) {
            Meci meci = meciOpt.get();
            meci.setScorAcasa(scorAcasa);
            meci.setScorDeplasare(scorDeplasare);
            meci.setStatus(status);
            matchRepository.save(meci);


            if (eliminaGazdele) {
                Team gazde = meci.getEchipaAcasa();
                gazde.setEliminated(true);
                teamRepository.save(gazde);
            }

            if (eliminaOaspetii) {
                Team oaspeti = meci.getEchipaDeplasare();
                oaspeti.setEliminated(true);
                teamRepository.save(oaspeti);
            }
        }

        return "redirect:/meciuri";
    }

    @PostMapping("/salveaza-meci")
    public String saveNewMatch(@ModelAttribute("meci") Meci meci) {

        meci.setStatus("PROGRAMAT");
        meci.setScorAcasa(0);
        meci.setScorDeplasare(0);


        matchRepository.save(meci);

        return "redirect:/meciuri";
    }
}