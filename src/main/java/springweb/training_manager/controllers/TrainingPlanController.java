package springweb.training_manager.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.entities.Weekdays;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.training_plan.TrainingPlansWrite;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineReadIndex;
import springweb.training_manager.services.TrainingPlanService;
import springweb.training_manager.services.TrainingRoutineService;
import springweb.training_manager.services.UserService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/plans")
public class TrainingPlanController {
    private final TrainingPlanService service;
    private final TrainingRoutineService routineService;
    private final Logger logger = LoggerFactory.getLogger(TrainingPlanController.class);

    public TrainingPlanController(
        final TrainingPlanService service,
        final TrainingRoutineService routineService
    ) {
        this.service = service;
        this.routineService = routineService;
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @GetMapping
    public String getAll(Authentication auth, Model model){
        var loggedUser = UserService.getUserByAuth(auth);

        List<TrainingRoutineReadIndex> plans = service.getAllByUser(loggedUser);

        model.addAttribute("plans", plans);
        model.addAttribute("weekdays", Weekdays.values());
        return "routine/index";
    }

    @Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
    @GetMapping("/week")
    public String getWeek(
        Authentication auth,
        Model model
    ){
        String userId = UserService.getUserIdByAuth(auth);
        Map<Weekdays, TrainingPlan> plans = null;
        try {
            plans = service.getUserActivePlans(userId);
        } catch(IllegalStateException ex){
            model.addAttribute("noActiveRoutineMess", ex.getMessage());
        } finally {
            model.addAttribute("plans", plans);
        }

        return "routine/week";
    }

    @GetMapping("/week/create")
    public String getWeekCreate(
        Model model
    ){
        var weekdays = Weekdays.values();
        TrainingPlansWrite toWrite = new TrainingPlansWrite();
        for(var weekday : weekdays)
            toWrite.add(weekday);

        model.addAttribute("plansWrite", toWrite);
        model.addAttribute("weekdays", weekdays);
        return "routine/save";
    }

    @PostMapping("/week/create")
    public RedirectView weekCreate(
        @ModelAttribute("schedulesList") TrainingPlansWrite schedulesList,
        Authentication auth,
        RedirectAttributes attributes
    ){
        // TODO: Test this action with acc: TestPlansCreate and pass as user
        try {
            List<TrainingPlan> created = service.createNewPlans(
                schedulesList,
                UserService.getUserByAuth(auth)
            );

            attributes.addFlashAttribute("messType", "success");
            attributes.addFlashAttribute("mess", "Stworzono nowy plan treningowy.");
        } catch(IllegalStateException ex) {
            attributes.addFlashAttribute("messType", "danger");
            attributes.addFlashAttribute("mess", ex.getMessage());
        }

        return new RedirectView("/plans/week/create");
    }
}
