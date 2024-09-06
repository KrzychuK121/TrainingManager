package springweb.training_manager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import springweb.training_manager.services.MyUserDetailsService;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    jsr250Enabled = true,
    securedEnabled = true,
    prePostEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {
    public static final String WELCOME_FIRST_NAME = "welcomeFirstName";
    public static final String WELCOME_LAST_NAME = "welcomeLastName";

    private final MyUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final PasswordEncoder passwordEncoder;

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
//            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS)) FIXME: It should be for API but not for views controllers
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
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
            .exceptionHandling(
                exep -> exep.accessDeniedPage("/access-denied")
            ).csrf(AbstractHttpConfigurer::disable)
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
