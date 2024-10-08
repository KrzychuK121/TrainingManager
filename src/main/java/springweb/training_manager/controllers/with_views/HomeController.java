package springweb.training_manager.controllers.with_views;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping
    String home(){
        return "../static/index";
    }

    @GetMapping("/glowna")
    String mainView(
        Model model
    ){
        model.addAttribute("title", "TrainingM - Strona główna");
        return "index2";
    }

    @GetMapping("/test")
    String test(){
        return "defaulttemplate";
    }
}
