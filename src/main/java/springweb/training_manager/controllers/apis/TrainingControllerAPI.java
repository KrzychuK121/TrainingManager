package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.view_models.training.TrainingCreate;
import springweb.training_manager.models.view_models.training.TrainingRead;
import springweb.training_manager.models.view_models.training.TrainingWriteAPI;
import springweb.training_manager.services.TrainingExerciseService;
import springweb.training_manager.services.TrainingService;
import springweb.training_manager.services.UserService;

import java.net.URI;
import java.util.List;

@RestController
@Secured(
    {
        Role.Constants.ADMIN,
        Role.Constants.MODERATOR,
        Role.Constants.USER
    }
)
@RequestMapping(
    value = "/api/training",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@Slf4j
public class TrainingControllerAPI {
    private final TrainingService service;
    private final TrainingExerciseService trainingExerciseService;

    @GetMapping("/paged")
    @ResponseBody
    ResponseEntity<Page<TrainingRead>> getAll(
        @PageableDefault(size = 2) Pageable page,
        Authentication auth
    ) {
        var user = UserService.getUserByAuth(auth);
        return ResponseEntity.ok(
            service.getPagedForUser(
                page,
                user
            )
        );
    }

    @GetMapping("/all")
    @ResponseBody
    ResponseEntity<List<TrainingRead>> getAll(
        Authentication auth
    ) {
        var user = UserService.getUserByAuth(auth);
        if (UserService.userIsInRole(user, Role.ADMIN))
            return ResponseEntity.ok(service.getAll());
        return ResponseEntity.ok(service.getPublicOrOwnerBy(user));
    }

    @GetMapping("/{id}")
    @ResponseBody
    ResponseEntity<TrainingRead> getById(
        @PathVariable int id,
        Authentication auth
    ) {
        try {
            var loggedUser = UserService.getUserByAuth(auth);
            return ResponseEntity.ok(
                new TrainingRead(
                    service.getByIdForUse(id, loggedUser)
                )
            );
        } catch (IllegalArgumentException e) {
            log.error("Wystąpił wyjątek: {}", e.getMessage());
            if (e.getMessage()
                .contains("Nie masz dostępu"))
                return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
            return ResponseEntity.notFound()
                .build();
        }
    }

    @GetMapping("/{id}/hasPrivateExercises")
    @ResponseBody
    ResponseEntity<Boolean> hasPrivateExercises(@PathVariable int id) {
        var hasPrivateExercises = trainingExerciseService.trainingContainsPrivateExercises(id);
        return ResponseEntity.ok(hasPrivateExercises);
    }

    @GetMapping(value = {"/createModel", "/createModel/{id}"})
    public ResponseEntity<TrainingCreate> getCreateModel(
        @PathVariable(required = false) Integer id,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        return ResponseEntity.ok(service.getCreateModel(id, loggedUser));
    }

    @PostMapping()
    @ResponseBody
    ResponseEntity<?> create(
        @RequestBody @Valid TrainingWriteAPI toCreate,
        BindingResult result,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        var validationErrors = service.validateAndPrepareTraining(
            toCreate,
            result,
            loggedUser
        );
        if (validationErrors != null)
            return ResponseEntity.badRequest()
                .body(validationErrors);

        Training created = service.create(toCreate.getToSave(), loggedUser);
        var trainingRead = new TrainingRead(created);
        return ResponseEntity.created(
                URI.create("/api/training/" + created.getId())
            )
            .body(trainingRead);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> edit(
        @RequestBody @Valid TrainingWriteAPI data,
        BindingResult result,
        @PathVariable int id,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        var validationErrors = service.validateAndPrepareTraining(
            data,
            result,
            loggedUser
        );
        if (validationErrors != null)
            return ResponseEntity.badRequest()
                .body(validationErrors);
        try {
            var toEdit = data.getToSave();
            service.edit(toEdit, id, loggedUser);
        } catch (IllegalArgumentException e) {
            log.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound()
                .build();
        }

        return ResponseEntity.noContent()
            .build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(
        @PathVariable int id,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        try {
            service.delete(id, loggedUser);
        } catch (NotOwnedByUserException e) {
            return ResponseEntity.badRequest()
                .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound()
                .build();
        } catch (Exception ex) {
            log.error("Wystąpił nieoczekiwany wyjątek: {}", ex.getMessage(), ex);
        }

        return ResponseEntity.noContent()
            .build();
    }
}
