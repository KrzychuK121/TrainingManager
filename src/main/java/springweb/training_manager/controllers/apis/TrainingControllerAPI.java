package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.controllers.TrainingController;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.models.viewmodels.training.TrainingWrite;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;
import springweb.training_manager.services.TrainingService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(
    value = "/api/training",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
public class TrainingControllerAPI {
    private final TrainingService service;
    private final ExerciseRepository exerciseRepo;
    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

    public TrainingControllerAPI(
            final TrainingService service,
            final ExerciseRepository exerciseRepo
    ) {
        this.service = service;
        this.exerciseRepo = exerciseRepo;
    }

    @GetMapping()
    @ResponseBody
    ResponseEntity<List<TrainingRead>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping()
    @ResponseBody
    ResponseEntity<TrainingRead> create(@RequestBody @Valid TrainingWrite toCreate){
        Training created = service.create(toCreate, null);
        var trainingRead = new TrainingRead(created);
        return ResponseEntity.created(
                URI.create("/training/" + created.getId())
        ).body(trainingRead);
    }


}
