package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.controllers.with_views.ExerciseController;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.viewmodels.exercise.ExerciseRead;
import springweb.training_manager.models.viewmodels.exercise.ExerciseWrite;
import springweb.training_manager.services.ExerciseService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(
    value = "/api/exercise",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class ExerciseControllerAPI {
    private final ExerciseService service;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ExerciseRead> getById(@PathVariable int id){
        try {
            Exercise found = service.getById(id);
            var foundRead = new ExerciseRead(found);
            return ResponseEntity.ok(foundRead);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping( "/api")
    @ResponseBody
    public ResponseEntity<List<ExerciseRead>> getAll(Pageable page){
        return ResponseEntity.ok(
            service.getAll(page)
                .getContent()
        );
    }

    @PostMapping(value = "/api")
    @ResponseBody
    public ResponseEntity<ExerciseRead> create(@RequestBody @Valid ExerciseWrite toSave){
        Exercise created = service.create(toSave);

        var exerciseRead = new ExerciseRead(created);
        return ResponseEntity.created(
            URI.create("/exercise/" + created.getId())
        ).body(exerciseRead);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> edit(
        @RequestBody @Valid ExerciseWrite toEdit,
        @PathVariable int id
    ){
        try {
            service.edit(toEdit, id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping( "/api/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable int id){
        try {
            service.delete(id);
        } catch(IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}
