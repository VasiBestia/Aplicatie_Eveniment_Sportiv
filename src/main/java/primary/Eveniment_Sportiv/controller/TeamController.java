/** Controller pentru gestionarea echipelor în cadrul evenimentului sportiv.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */

package primary.Eveniment_Sportiv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;

import primary.Eveniment_Sportiv.model.Participant;
import primary.Eveniment_Sportiv.model.Sport;
import primary.Eveniment_Sportiv.model.Team;
import primary.Eveniment_Sportiv.model.UserAccount;
import primary.Eveniment_Sportiv.repository.ParticipantRepository;
import primary.Eveniment_Sportiv.repository.SportRepository;
import primary.Eveniment_Sportiv.repository.TeamRepository;
import primary.Eveniment_Sportiv.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class TeamController {

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;


    @GetMapping("/my-team")
    public String showMyTeamPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        Optional<UserAccount> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return "redirect:/login";

        Optional<Participant> partOpt = participantRepository.findByUserAccount(userOpt.get());
        if (partOpt.isEmpty()) return "redirect:/profile";

        Participant me = partOpt.get();


        List<Team> teamsAsCaptain = teamRepository.findAllByCapitan(me);


        List<Team> allTeams = teamRepository.findAll();
        List<Team> teamsAsMember = new ArrayList<>();

        for (Team t : allTeams) {

            if (t.getMembri().contains(me) && !t.getCapitan().getIdParticipant().equals(me.getIdParticipant())) {
                teamsAsMember.add(t);
            }
        }


        boolean hasAnyTeam = !teamsAsCaptain.isEmpty() || !teamsAsMember.isEmpty();

        model.addAttribute("hasAnyTeam", hasAnyTeam);
        model.addAttribute("teamsAsCaptain", teamsAsCaptain);
        model.addAttribute("teamsAsMember", teamsAsMember);

        return "my_team";
    }



    @GetMapping("/creare-echipa")
    public String showCreateTeamForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";


        List<Sport> sporturi = sportRepository.findAll();
        model.addAttribute("sporturi", sporturi);

        return "create_team";
    }

    @GetMapping("/join-team")
    public String joinTeamViaGet(
            @RequestParam(value = "codInscriere", required = false) String codInscriere,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        if (codInscriere == null || codInscriere.trim().isEmpty()) {

            return "redirect:/my-team";
        }


        return joinTeam(codInscriere, session, redirectAttributes);
    }

    @PostMapping("/creare-echipa")
    public String createTeam(
            @RequestParam("numeEchipa") String numeEchipa,
            @RequestParam("sportId") Long sportId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        try {

            Optional<UserAccount> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {

                return "redirect:/login";
            }

            Optional<Participant> partOpt = participantRepository.findByUserAccount(userOpt.get());


            if (partOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Trebuie să îți completezi datele personale (Profil) înainte de a crea o echipă!");
                return "redirect:/profile";
            }
            Participant capitan = partOpt.get();


            if (teamRepository.existsByNumeEchipa(numeEchipa)) {
                redirectAttributes.addFlashAttribute("error", "Există deja o echipă cu numele '" + numeEchipa + "'. Alege altul.");
                return "redirect:/creare-echipa";
            }


            Optional<Sport> sportOpt = sportRepository.findById(sportId);
            if (sportOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Sportul selectat nu este valid.");
                return "redirect:/creare-echipa";
            }

            if (teamRepository.existsByCapitanAndSportIdSport(capitan, sportId)) {
                redirectAttributes.addFlashAttribute("error", "Ești deja căpitanul unei echipe înscrise la acest sport!");
                return "redirect:/creare-echipa";
            }


            if (sportOpt.isEmpty()) {
                Team newTeam = new Team();
                newTeam.setNumeEchipa(numeEchipa);
                newTeam.setSport(sportOpt.get());
                newTeam.setCapitan(capitan);


                newTeam.addMembru(capitan);


                teamRepository.save(newTeam);

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Felicitări! Echipa " + newTeam.getNumeEchipa() + " a fost creată. Codul tău unic de invitare este: " + newTeam.getCodInscriere()
                );
            }


            return "redirect:/my-team";

        } catch (Exception e) {

            e.printStackTrace();

            redirectAttributes.addFlashAttribute("error", "Eroare server: A apărut o problemă la salvarea datelor.");
        }


        return "redirect:/creare-echipa";
    }

    @PostMapping("/join-team")
    public String joinTeam(
            @RequestParam("codInscriere") String codInscriere,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        try {

            Optional<UserAccount> userOpt = userRepository.findById(userId);
            Optional<Participant> partOpt = participantRepository.findByUserAccount(userOpt.get());

            if (partOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Completează profilul înainte de a intra într-o echipă.");
                return "redirect:/profile";
            }
            Participant participant = partOpt.get();


            Optional<Team> teamOpt = teamRepository.findByCodInscriere(codInscriere.trim());

            if (teamOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cod invalid! Nu am găsit nicio echipă cu acest cod.");
                return "redirect:/my-team";
            }
            Team team = teamOpt.get();


            if (team.getMembri().contains(participant)) {
                redirectAttributes.addFlashAttribute("error", "Ești deja membru în echipa " + team.getNumeEchipa() + "!");
                return "redirect:/my-team";
            }


            if (team.getMembri().size() >= team.getSport().getTeamSize()) {
                redirectAttributes.addFlashAttribute("error", "Echipa este completă (" + team.getSport().getTeamSize() + " jucători). Nu te mai poți înscrie.");
                return "redirect:/my-team";
            }


            team.addMembru(participant);
            teamRepository.save(team);

            redirectAttributes.addFlashAttribute("success", "Te-ai alăturat cu succes echipei " + team.getNumeEchipa() + "!");
            return "redirect:/my-team";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Eroare la înscriere: " + e.getMessage());
            return "redirect:/my-team";
        }
    }
}