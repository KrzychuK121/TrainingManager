package springweb.trainingmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import springweb.trainingmanager.models.entities.Role;
import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.repositories.forcontrollers.RoleRepository;
import springweb.trainingmanager.repositories.forcontrollers.UserRepository;

import java.util.Set;

@Controller
public class UserManagerController {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private static final Logger logger = LoggerFactory.getLogger(UserManagerController.class);


    public UserManagerController(
        final UserRepository userRepo,
        final RoleRepository roleRepo,
        final PasswordEncoder encoder
    ) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
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

    /*@GetMapping("/seed")
    String seed(){
        String pass = "admin1234";
        User admin = new User();
        admin.setUsername("admin");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setPassword(pass);
        admin.setPasswordHashed(encoder.encode(pass));
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        admin.setRoles(Set.of(role));
        role.setUsers(Set.of(admin));
        roleRepo.save(role);
        userRepo.save(admin);
        return "index2";
    }*/

}
