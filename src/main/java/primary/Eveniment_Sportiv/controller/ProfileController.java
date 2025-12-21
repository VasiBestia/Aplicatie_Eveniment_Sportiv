package primary.Eveniment_Sportiv.controller;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import primary.Eveniment_Sportiv.MvcConfig;
import primary.Eveniment_Sportiv.model.UserAccount;
import primary.Eveniment_Sportiv.model.Participant;
import primary.Eveniment_Sportiv.repository.UserRepository;
import primary.Eveniment_Sportiv.repository.ParticipantRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @GetMapping("/profile")
    public String showProfilePage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<UserAccount> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        UserAccount user = userOpt.get();

        Optional<Participant> partOpt = participantRepository.findByUserAccount(user);
        Participant participant = partOpt.orElse(new Participant());

        String profilePic = user.getImagePath();
        if (profilePic == null || profilePic.isEmpty()) {
            profilePic = "/img/undraw_profile.svg";
        }

        String finalPicUrl = profilePic + "?v=" + System.currentTimeMillis();

        model.addAttribute("user", user);
        model.addAttribute("participant", participant);
        model.addAttribute("profilePicUrl", finalPicUrl);

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam("nume") String nume,
            @RequestParam("prenume") String prenume,
            @RequestParam("facultate") String facultate,
            @RequestParam(value = "anStudiu", required = false) Integer anStudiu,
            @RequestParam("file_poza") MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<UserAccount> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Eroare: Utilizatorul nu a fost găsit.");
            return "redirect:/login";
        }
        UserAccount user = userOpt.get();

        try {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                String cleanFileName = userId + "_" + fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

                Path uploadDir = Paths.get(MvcConfig.UPLOAD_DIRECTORY);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                try (InputStream inputStream = file.getInputStream()) {
                    Path filePath = uploadDir.resolve(cleanFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                    String dbPath = "/uploads/" + cleanFileName;
                    user.setImagePath(dbPath);
                    userRepository.save(user);

                    session.setAttribute("profile_pic", dbPath);
                    redirectAttributes.addFlashAttribute("success", "Poza de profil a fost încărcată!");

                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "Eroare la salvarea imaginii pe server.");
                    return "redirect:/profile";
                }
            }

            Optional<Participant> partOpt = participantRepository.findByUserAccount(user);
            Participant participant;

            if (partOpt.isPresent()) {
                participant = partOpt.get();
            } else {
                participant = new Participant();
                participant.setUserAccount(user);
            }

            participant.setNume(nume);
            participant.setPrenume(prenume);
            participant.setFacultate(facultate);
            participant.setAnStudiu(anStudiu);

            participantRepository.save(participant);

            if (!redirectAttributes.containsAttribute("success")) {
                redirectAttributes.addFlashAttribute("success", "Detalii profil actualizate!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Eroare server la actualizare: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}