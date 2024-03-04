package springweb.trainingmanager.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.models.viewmodels.user.MyUserDetails;

@Controller
public class HomeController {
    @GetMapping
    String home(){
        return "../static/index";
    }

    @GetMapping("/glowna")
    String mainView(
        Model model,
        HttpSession session,
        Authentication auth
    ){
        if(auth != null && auth.isAuthenticated()){
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            User loggedUser = userDetails.getUser();
            String[] welcomeInfo = { loggedUser.getFirstName(), loggedUser.getLastName() };
            session.setAttribute("welcomeInfo", welcomeInfo);
        }

        model.addAttribute("title", "TrainingM - Strona główna");
        return "index2";
    }

    @GetMapping("/test")
    String test(){
        return "defaulttemplate";
    }
}
