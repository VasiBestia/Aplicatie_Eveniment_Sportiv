/** Controller pentru gestionarea autentificării utilizatorilor, inclusiv login, înregistrare,
 * resetare parolă și deconectare.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */


package primary.Eveniment_Sportiv.controller;

import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import primary.Eveniment_Sportiv.model.UserAccount;
import primary.Eveniment_Sportiv.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;



    @GetMapping("/login")
    public String showLoginPage(
            @CookieValue(value = "remember_email", required = false) String rememberedEmail,
            Model model
    ) {

        model.addAttribute("remembered_email", rememberedEmail);
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @GetMapping("/retype-password")
    public String showRetypePasswordPage() {
        return "retype_password";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();


        return "redirect:/login";
    }



    @PostMapping("/login")
    public String loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "remember_me", required = false) String rememberMe,
            HttpSession session,
            HttpServletResponse response,
            Model model
    ) {

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            model.addAttribute("error", "ERROR: Introduceti toate campurile.");
            return "login";
        }

        try {

            Optional<UserAccount> userRecord = userRepository.findByEmail(email);

            if (userRecord.isEmpty()) {
                model.addAttribute("error", "ERROR: Email sau parola incorecta.");
                return "login";
            }

            UserAccount user = userRecord.get();
            String storedHashedPassword = user.getParola();


            if (!passwordEncoder.matches(password, storedHashedPassword)) {
                model.addAttribute("error", "ERROR: Email sau parola incorecta.");
                return "login";
            }


            session.setAttribute("logged_in", true);
            session.setAttribute("user_id", user.getIdUser());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("email", user.getEmail());

            String pozaUser = (user.getImagePath() != null) ? user.getImagePath() : "default_avatar.png";
            session.setAttribute("profile_pic", pozaUser);


            if (rememberMe != null) {
                Cookie cookie = new Cookie("remember_email", email);
                cookie.setMaxAge(30 * 24 * 60 * 60);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                response.addCookie(cookie);
            } else {

                Cookie cookie = new Cookie("remember_email", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }


            return "redirect:/index";

        } catch (Exception e) {

            System.err.println("Eroare la autentificare: " + e.getMessage());
            model.addAttribute("error", "ERROR: Eroare internă a serverului.");
            return "login";
        }
    }



    @PostMapping("/register")
    public String registerUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("retype_password") String retypePassword,
            Model model // <--- Esențial pentru a trimite eroarea în HTML
    ) {
        // 1. Verificăm câmpurile goale
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || !password.equals(retypePassword)) {
            model.addAttribute("error", "Te rog completează toate câmpurile și asigură-te că parolele se potrivesc.");
            return "register"; // Rămânem pe pagina de register
        }

        // 2. Verificăm lungimea parolei
        if (password.length() < 8) {
            model.addAttribute("error", "Parola trebuie să aibă minim 8 caractere.");
            return "register";
        }

        // 3. Verificăm complexitatea parolei (Caractere speciale)
        String specialCharsPattern = ".*[!@#$%^&*(),.?\":{}|<>].*";
        if (!password.matches(specialCharsPattern)) {
            model.addAttribute("error", "Parola trebuie să conțină cel puțin un caracter special.");
            return "register";
        }

        // 4. Verificăm litere
        if (!password.matches(".*[a-zA-Z].*")) {
            model.addAttribute("error", "Parola trebuie să conțină cel puțin o literă.");
            return "register";
        }

        // 5. Verificăm cifre
        if (!password.matches(".*\\d.*")) {
            model.addAttribute("error", "Parola trebuie să conțină cel puțin o cifră.");
            return "register";
        }

        // 6. Verificăm formatul de Email (Regex)
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!email.matches(emailRegex)) {
            model.addAttribute("error", "Formatul adresei de email este invalid.");
            return "register";
        }

        try {
            // 7. Verificăm duplicatele în baza de date
            if (userRepository.existsByUsername(username)) {
                model.addAttribute("error", "Acest nume de utilizator este deja folosit.");
                return "register";
            }
            if (userRepository.existsByEmail(email)) {
                model.addAttribute("error", "Acest email este deja înregistrat.");
                return "register";
            }

            // 8. Totul e OK -> Salvăm Userul
            String hashedPassword = passwordEncoder.encode(password);

            UserAccount newUser = new UserAccount();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setParola(hashedPassword);
            // Dacă ai câmp 'provider', poți seta: newUser.setProvider("LOCAL");

            userRepository.save(newUser);

            // 9. Redirect către login cu un mic parametru de succes (opțional)
            // Nu folosim Model aici pentru că redirect-ul șterge modelul.
            return "redirect:/login";

        } catch (Exception e) {
            System.err.println("Eroare la înregistrare: " + e.getMessage());
            model.addAttribute("error", "Eroare de server la înregistrare. Încearcă din nou.");
            return "register";
        }
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> processForgotPassword(
            @RequestParam("email") String email,
            HttpSession session
    ) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("ERROR: Te rog completează adresa de email.");
        }

        try {
            Optional<UserAccount> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                session.setAttribute("reset_email", email);
                return ResponseEntity.status(HttpStatus.SEE_OTHER)
                        .header("Location", "/retype-password")
                        .build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: Email-ul nu a fost găsit în baza de date.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare server la verificarea email-ului.");
        }
    }

    @PostMapping("/retype-password")
    public ResponseEntity<?> processRetypePassword(
            @RequestParam("password") String password,
            @RequestParam("retype_password") String retypePassword,
            HttpSession session
    ) {
        String emailToUpdate = (String) session.getAttribute("reset_email");

        if (emailToUpdate == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR: Sesiunea de resetare a expirat. Te rog reia procedura.");
        }

        if (password == null || password.isEmpty() || !password.equals(retypePassword)) {
            return ResponseEntity.badRequest().body("ERROR: Te rog completează ambele câmpuri și asigură-te că parolele se potrivesc.");
        }

        try {
            Optional<UserAccount> userOptional = userRepository.findByEmail(emailToUpdate);

            if (userOptional.isPresent()) {
                UserAccount user = userOptional.get();
                String hashedPassword = passwordEncoder.encode(password);
                user.setParola(hashedPassword);
                userRepository.save(user);

                session.removeAttribute("reset_email");

                return ResponseEntity.status(HttpStatus.SEE_OTHER)
                        .header("Location", "/login")
                        .body("Parola a fost schimbată cu succes.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: Nu s-a putut actualiza parola. Userul nu mai există.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare server la resetarea parolei.");
        }
    }
}