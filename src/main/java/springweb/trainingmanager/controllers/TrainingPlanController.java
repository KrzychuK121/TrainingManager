package springweb.trainingmanager.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import springweb.trainingmanager.models.entities.TrainingPlan;
import springweb.trainingmanager.models.entities.TrainingSchedule;
import springweb.trainingmanager.models.entities.Weekdays;
import springweb.trainingmanager.models.viewmodels.trainingPlan.TrainingPlansWrite;
import springweb.trainingmanager.services.TrainingPlanService;
import springweb.trainingmanager.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/plans")
public class TrainingPlanController {
    private final TrainingPlanService service;
    private final Logger logger = LoggerFactory.getLogger(TrainingPlanController.class);

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
    public String getWeekCreate(
        Model model
    ){
        var weekdays = Weekdays.values();
        TrainingPlansWrite toWrite = new TrainingPlansWrite();
        for(var weekday : weekdays)
            toWrite.add(weekday);

        model.addAttribute("schedulesList", toWrite);
        model.addAttribute("weekdays", weekdays);
        return "routine/save";
    }

    @PostMapping("/week/create")
    public RedirectView weekCreate(
        @ModelAttribute("schedulesList") TrainingPlansWrite schedulesList,
        Authentication auth
    ){
        var weekdays = Weekdays.values();
        List<TrainingPlan> plans = new ArrayList<>(weekdays.length);
        for(var weekday : weekdays) {
            var trainingId = schedulesList.getSchedules().get(weekday);
            var toAdd = new TrainingPlan(
                UserService.getUserByAuth(auth),
                new TrainingSchedule(trainingId, weekday)
            );
            plans.add(toAdd);
        }

        for(var plan : plans){
            var schedule = plan.getTrainingSchedule();
            var routine = plan.getTrainingRoutine();
            logger.info("plan: ");
            logger.info("trainingId: " + schedule.getTrainingId());
            logger.info("weekday: " + schedule.getWeekday());
            logger.info("user: " + routine.getOwner().getFirstName());
            logger.info("active: " + routine.isActive());
        }

        return new RedirectView("/plans/week/create");
    }
}
