package springweb.training_manager.controllers.with_views;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.viewmodels.user.UserWrite;
import springweb.training_manager.services.UserService;

import javax.naming.AuthenticationException;

@Controller
public class UserManagerController {
    private final UserService service;
    private final PasswordEncoder encoder;
    private static final Logger logger = LoggerFactory.getLogger(UserManagerController.class);


    public UserManagerController(
        final UserService service,
        final PasswordEncoder encoder
    ) {
        this.service = service;
        this.encoder = encoder;
    }

    @ModelAttribute("title")
    String getTitle() {
        return "TrainingM - Użytkownik";
    }

    @GetMapping("/login")
    String login(Authentication auth) {
        if (auth != null && auth.isAuthenticated())
            return "../static/index";
        return "user_manager/login";
    }

    @GetMapping("/glowna/login")
    String mainLogin(Authentication auth) {
        if (auth != null && auth.isAuthenticated())
            return "../static/index";
        return "user_manager/login";
    }

    @PostMapping("/loginErr")
    String loginErr(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = "Nieprawidłowy login lub hasło.";
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        model.addAttribute("mess", errorMessage);
        model.addAttribute("messType", "danger");
        return "user_manager/login";
    }


    @GetMapping("/register")
    String register(Model model) {
        model.addAttribute("user", new UserWrite());
        return "user_manager/register";
    }

    @PostMapping("/register")
    String register(
        @ModelAttribute("user") @Valid UserWrite current,
        BindingResult result,
        Model model
    ) {
        if (!service.ifPasswordsMatches(current.getPassword(), current.getPasswordRepeat()))
            result.addError(new ObjectError("passwordRepeat", UserService.PASSWORDS_NOT_EQUAL_MESSAGE));
        if (result.hasErrors())
            return "user_manager/register";

        try {
            service.register(current, Role.USER);
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", e.getMessage());
            return "user_manager/register";
        }

        model.addAttribute("messType", "success");
        model.addAttribute("mess", "Rejestracja przeszła pomyślnie!\nSpróbuj się zalogować");
        return "user_manager/login";
    }

    @GetMapping("/access-denied")
    String accessDenied() {
        return "user_manager/access_denied";
    }

    @GetMapping("/logout-success")
    String logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException {
        return "user_manager/logout";
    }

}
