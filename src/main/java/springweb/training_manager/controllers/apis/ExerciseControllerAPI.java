package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseCreate;
import springweb.training_manager.models.viewmodels.exercise.ExerciseRead;
import springweb.training_manager.models.viewmodels.exercise.ExerciseWriteAPI;
import springweb.training_manager.services.ExerciseService;

import java.net.URI;

@RestController
@Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
@RequestMapping(
    value = "/api/exercise",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class ExerciseControllerAPI {
    private final ExerciseService service;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseControllerAPI.class);

    @GetMapping()
    @ResponseBody
    public ResponseEntity<Page<ExerciseRead>> getAll(@PageableDefault(size = 2) Pageable page) {
        return ResponseEntity.ok(service.getAll(page));
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ExerciseRead> getById(@PathVariable int id) {
        try {
            Exercise found = service.getById(id);
            var foundRead = new ExerciseRead(found);
            return ResponseEntity.ok(foundRead);
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = {"/createModel", "/createModel/{id}"})
    public ResponseEntity<ExerciseCreate> getCreateModel(@PathVariable(required = false) Integer id) {
        return ResponseEntity.ok(service.getCreateModel(id));
    }

    @PostMapping()
    @ResponseBody
    public ResponseEntity<?> create(
        @RequestBody @Valid ExerciseWriteAPI data,
        BindingResult result
    ) {
        var validationErrors = service.validateAndPrepareExercise(data, result);
        if (validationErrors != null)
            return ResponseEntity.badRequest().body(validationErrors);

        Exercise created = service.create(data.getToSave());

        var exerciseRead = new ExerciseRead(created);
        return ResponseEntity.created(
            URI.create("/exercise/" + created.getId())
        ).body(exerciseRead);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> edit(
        @RequestBody @Valid ExerciseWriteAPI data,
        BindingResult result,
        @PathVariable int id
    ) {
        var validationErrors = service.validateAndPrepareExercise(data, result);
        if (validationErrors != null)
            return ResponseEntity.badRequest().body(validationErrors);
        try {
            var toEdit = data.getToSave();
            service.edit(toEdit, id);
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            service.delete(id);
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}
