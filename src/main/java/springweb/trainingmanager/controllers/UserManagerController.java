package springweb.trainingmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import springweb.trainingmanager.models.entities.Role;
import springweb.trainingmanager.models.viewmodels.user.UserWrite;
import springweb.trainingmanager.services.UserService;

import javax.naming.AuthenticationException;
import java.util.Set;

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
    String getTitle(){
        return "TrainingM - Użytkownik";
    }
    
    @GetMapping("/login")
    String login(){
        return "userManager/login";
    }

    @PostMapping("/loginErr")
    String loginErr(HttpServletRequest request, Model model){
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
        return "userManager/login";
    }

    
    @GetMapping("/register")
    String register(Model model){
        model.addAttribute("user", new UserWrite());
        return "userManager/register";
    }

    @PostMapping("/register")
    String register(
        @ModelAttribute("user") @Valid UserWrite current,
        BindingResult result,
        Model model
    ){
        if(!service.ifPasswordsMatches(current.getPassword(), current.getPasswordRepeat()))
            result.addError(new ObjectError("passwordRepeat", UserService.PASSWORDS_NOT_EQUAL_MESSAGE));
        if(result.hasErrors())
            return "userManager/register";

        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");

        try {
            service.register(current, Set.of(roleUser));
        } catch (IllegalArgumentException e){
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            model.addAttribute("messType", "danger");
            model.addAttribute("mess", e.getMessage());
            return "userManager/register";
        }

        model.addAttribute("messType", "success");
        model.addAttribute("mess", "Rejestracja przeszła pomyślnie!\nSpróbuj się zalogować");
        return "userManager/login";
    }

    @GetMapping("/access-denied")
    String accessDenied(){
        return "userManager/accessDenied";
    }

    @GetMapping("/logout")
    String logout(
        HttpServletRequest request,
        HttpSession session
    ) throws ServletException {
        if(session.getAttribute("welcomeInfo") != null){
            session.removeAttribute("welcomeInfo");
        }

        request.logout();
        return "userManager/logout";
    }

}
