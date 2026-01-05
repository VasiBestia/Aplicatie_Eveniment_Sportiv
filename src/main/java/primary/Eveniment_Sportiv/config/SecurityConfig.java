/** Clasa pentru crearea Securitatii Aplicatiei.
 * @author Vasilescu Alexandru Gabriel
 * @version 5 Ianuarie 2026
 */

package primary.Eveniment_Sportiv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import primary.Eveniment_Sportiv.config.CustomOAuth2UserService;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private GoogleLoginSuccessHandler googleLoginSuccessHandler;

    @Autowired
    private FacebookLoginSuccessHandler facebookLoginSuccessHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form.disable())


                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            String clientRegId = ((org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication)
                                    .getAuthorizedClientRegistrationId();

                            if ("facebook".equals(clientRegId)) {
                                facebookLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                            } else if ("google".equals(clientRegId)) {
                                googleLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                            }
                        })
                );


        return http.build();
    }
}