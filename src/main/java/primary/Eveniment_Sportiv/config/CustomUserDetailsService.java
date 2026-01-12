package primary.Eveniment_Sportiv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import primary.Eveniment_Sportiv.model.UserAccount;
import primary.Eveniment_Sportiv.repository.UserRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Căutăm userul în baza de date după email (fiindcă în login.html am pus name="username", dar userul scrie email)
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizatorul nu a fost găsit cu email-ul: " + email));

        // 2. Returnăm un obiect de tip User (din Spring Security)
        // Aici Spring va compara automat parola hashed din DB cu cea introdusă în formular
        return new User(
                user.getEmail(),
                user.getParola(), // Parola trebuie sa fie criptată cu BCrypt în baza de date!
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Sau user.getRole() dacă ai roluri
        );
    }
}