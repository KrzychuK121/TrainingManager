package springweb.trainingmanager.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import springweb.trainingmanager.models.entities.TrainingPlan;
import springweb.trainingmanager.models.entities.Weekdays;
import springweb.trainingmanager.services.TrainingPlanService;
import springweb.trainingmanager.services.UserService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/plans")
public class TrainingPlanController {
    private final TrainingPlanService service;

    public TrainingPlanController(
        final TrainingPlanService service
    ) {
        this.service = service;
    }

    @GetMapping("/week")
    public String getWeek(
        Authentication auth,
        Model model
    ){
        String userId = UserService.getUserIdByAuth(auth);
        try {
            Map<Weekdays, TrainingPlan> plans = service.getUserActivePlans(userId);
            model.addAttribute("plans", plans);
        } catch(IllegalStateException ex) {
            ex.printStackTrace();
        } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        return "routine/week";
    }

    @GetMapping("/week/create")
    public String getWeekCreate(Model model){
        model.addAttribute("weekdays", Weekdays.values());
        return "routine/save";
    }

    @PostMapping("/week/create")
    public RedirectView weekCreate(){

        return new RedirectView("/plans/week/create");
    }
}
