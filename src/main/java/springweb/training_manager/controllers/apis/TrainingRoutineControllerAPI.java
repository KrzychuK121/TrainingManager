package springweb.training_manager.controllers.apis;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineRead;
import springweb.training_manager.services.TrainingRoutineService;

import java.util.List;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class TrainingRoutineControllerAPI {

    private final TrainingRoutineService service;

    @GetMapping
    public ResponseEntity<List<TrainingRoutineRead>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }
}
