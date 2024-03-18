package springweb.trainingmanager.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.net.HttpCookie;
import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    jsr250Enabled = true,
    securedEnabled = true,
    prePostEnabled = true
)
public class SecurityConfig {
    public static final String WELCOME_FIRST_NAME = "welcomeFirstName";
    public static final String WELCOME_LAST_NAME = "welcomeLastName";
    public SecurityConfig() { }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(
                authz -> authz
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/glowna").permitAll()
                    .requestMatchers("/exercise/api").permitAll()
                    .requestMatchers("/training/api").permitAll()
                    .requestMatchers("/logout").authenticated()
                    .anyRequest().permitAll()
            )
            .formLogin(
                formLogin -> formLogin.loginPage("/login").permitAll()
                    .defaultSuccessUrl("/glowna")
                    .failureForwardUrl("/loginErr")
            )
            .rememberMe(
                customize -> customize.key("K8s#gs*3js(*3jsf89SadaJS*#@")
            )
            .logout(
                logout -> logout.logoutSuccessUrl("/logout-success")
                    .deleteCookies(
                        SecurityConfig.WELCOME_FIRST_NAME,
                        SecurityConfig.WELCOME_LAST_NAME
                    )
            )
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
