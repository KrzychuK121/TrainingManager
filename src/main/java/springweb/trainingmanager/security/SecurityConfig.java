package springweb.trainingmanager.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.models.viewmodels.user.MyUserDetails;
import springweb.trainingmanager.services.MyUserDetailsService;

import java.io.IOException;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    jsr250Enabled = true,
    securedEnabled = true,
    prePostEnabled = true
)
public class SecurityConfig {
    private final HttpSession session;

    public SecurityConfig(
        final HttpSession session
    ) {
        this.session = session;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(
                authz -> authz
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/glowna").permitAll()
                    .requestMatchers("/exercise/api").permitAll()
                    .requestMatchers("/training/api").permitAll()
                    .anyRequest().permitAll()
            )
            .formLogin(
                formLogin -> formLogin.loginPage("/login").permitAll()
                    .defaultSuccessUrl("/glowna")
                    .failureForwardUrl("/loginErr")
                    .successHandler(
                        new SavedRequestAwareAuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
                                MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
                                User loggedUser = userDetails.getUser();
                                String[] welcomeInfo = { loggedUser.getFirstName(), loggedUser.getLastName() };
                                session.setAttribute("welcomeInfo", welcomeInfo);

                                super.onAuthenticationSuccess(request, response, authentication);
                            }
                        }
                    )
            )
            .logout(withDefaults())
            .exceptionHandling( exep -> exep
                .accessDeniedPage("/access-denied")
            ).csrf(AbstractHttpConfigurer::disable)
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
