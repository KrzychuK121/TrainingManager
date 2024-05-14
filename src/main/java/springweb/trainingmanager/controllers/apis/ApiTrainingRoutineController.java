package springweb.trainingmanager.controllers.apis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springweb.trainingmanager.models.entities.TrainingRoutine;
import springweb.trainingmanager.models.viewmodels.trainingRoutine.TrainingRoutineRead;
import springweb.trainingmanager.services.TrainingRoutineService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/routines/api")
public class ApiTrainingRoutineController {

    private final TrainingRoutineService service;

    public ApiTrainingRoutineController(
        final TrainingRoutineService service
    ) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TrainingRoutineRead>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }
}
