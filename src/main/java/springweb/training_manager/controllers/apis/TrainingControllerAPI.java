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
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.training.TrainingCreate;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.models.viewmodels.training.TrainingWriteAPI;
import springweb.training_manager.services.TrainingService;
import springweb.training_manager.services.UserService;

import java.net.URI;
import java.util.List;

@RestController
@Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
@RequestMapping(
    value = "/api/training",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class TrainingControllerAPI {
    private final TrainingService service;
    private static final Logger logger = LoggerFactory.getLogger(TrainingControllerAPI.class);

    @GetMapping()
    @ResponseBody
    ResponseEntity<Page<TrainingRead>> getAll(@PageableDefault(size = 2) Pageable page) {
        return ResponseEntity.ok(service.getAllPagedAlternative(page));
    }

    @GetMapping("/all")
    @ResponseBody
    ResponseEntity<List<TrainingRead>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping(value = {"/createModel", "/createModel/{id}"})
    public ResponseEntity<TrainingCreate> getCreateModel(
        @PathVariable(required = false) Integer id,
        Authentication auth
    ) {
        var userId = UserService.getUserIdByAuth(auth);
        return ResponseEntity.ok(service.getCreateModel(id, userId));
    }

    @PostMapping()
    @ResponseBody
    ResponseEntity<?> create(
        @RequestBody @Valid TrainingWriteAPI toCreate,
        BindingResult result,
        Authentication auth
    ) {
        var validationErrors = service.validateAndPrepareTraining(toCreate, result);
        if (validationErrors != null)
            return ResponseEntity.badRequest().body(validationErrors);

        var userId = UserService.getUserIdByAuth(auth);
        Training created = service.create(toCreate.getToSave(), userId);
        var trainingRead = new TrainingRead(created);
        return ResponseEntity.created(
            URI.create("/api/training/" + created.getId())
        ).body(trainingRead);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> edit(
        @RequestBody @Valid TrainingWriteAPI data,
        BindingResult result,
        @PathVariable int id,
        Authentication auth
    ) {
        var userId = UserService.getUserIdByAuth(auth);
        var validationErrors = service.validateAndPrepareTraining(data, result);
        if (validationErrors != null)
            return ResponseEntity.badRequest().body(validationErrors);
        try {
            var toEdit = data.getToSave();
            service.edit(toEdit, id, userId);
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(
        @PathVariable int id,
        Authentication auth
    ) {
        var userId = UserService.getUserIdByAuth(auth);
        try {
            service.delete(id, userId);
        } catch (NotOwnedByUserException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("Wystąpił nieoczekiwany wyjątek: {}", ex.getMessage(), ex);
        }

        return ResponseEntity.noContent().build();
    }
}
