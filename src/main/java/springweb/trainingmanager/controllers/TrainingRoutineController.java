package springweb.trainingmanager.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springweb.trainingmanager.models.entities.TrainingRoutine;
import springweb.trainingmanager.models.viewmodels.trainingRoutine.TrainingRoutineRead;
import springweb.trainingmanager.services.TrainingRoutineService;
import springweb.trainingmanager.services.UserService;

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
