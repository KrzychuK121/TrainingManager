package springweb.training_manager.controllers.with_views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springweb.training_manager.services.TrainingRoutineService;

@Controller
@RequestMapping("/routines")
public class TrainingRoutineController {

    private final TrainingRoutineService service;

    public TrainingRoutineController(
        final TrainingRoutineService service
    ) {
        this.service = service;
    }
}
