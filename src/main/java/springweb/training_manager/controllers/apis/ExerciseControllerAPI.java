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
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseCreate;
import springweb.training_manager.models.viewmodels.exercise.ExerciseRead;
import springweb.training_manager.models.viewmodels.exercise.ExerciseWriteAPI;
import springweb.training_manager.services.ExerciseService;
import springweb.training_manager.services.UserService;

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

    @GetMapping("/paged")
    @ResponseBody
    public ResponseEntity<Page<ExerciseRead>> getPagedForUser(
        @PageableDefault(size = 2) Pageable page,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        var paged = service.getPagedForUser(page, loggedUser);
        return ResponseEntity.ok(paged);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ExerciseRead> getById(
        @PathVariable int id,
        Authentication auth
    ) {
        try {
            var loggedUser = UserService.getUserByAuth(auth);
            Exercise found = service.getByIdForUse(id, loggedUser);
            var foundRead = new ExerciseRead(found);
            return ResponseEntity.ok(foundRead);
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound()
                .build();
        }
    }

    @GetMapping(value = {"/createModel", "/createModel/{id}"})
    public ResponseEntity<ExerciseCreate> getCreateModel(
        @PathVariable(required = false) Integer id,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        return ResponseEntity.ok(
            service.getCreateModel(
                id,
                loggedUser
            )
        );
    }

    @PostMapping()
    @ResponseBody
    public ResponseEntity<?> create(
        @RequestBody @Valid ExerciseWriteAPI data,
        BindingResult result,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        var validationErrors = service.validateAndPrepareExercise(
            data,
            result,
            loggedUser
        );
        if (validationErrors != null)
            return ResponseEntity.badRequest()
                .body(validationErrors);

        Exercise created = service.create(
            data.getToSave(),
            loggedUser
        );

        var exerciseRead = new ExerciseRead(created);
        return ResponseEntity.created(
                URI.create("/exercise/" + created.getId())
            )
            .body(exerciseRead);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> edit(
        @RequestBody @Valid ExerciseWriteAPI data,
        BindingResult result,
        @PathVariable int id,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        var validationErrors = service.validateAndPrepareExercise(
            data,
            result,
            loggedUser
        );
        if (validationErrors != null)
            return ResponseEntity.badRequest()
                .body(validationErrors);
        try {
            var toEdit = data.getToSave();
            service.edit(
                toEdit,
                id,
                loggedUser
            );
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
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
        try {
            var loggedUser = UserService.getUserByAuth(auth);
            service.delete(id, loggedUser);
        } catch (NotOwnedByUserException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.badRequest()
                .build();
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound()
                .build();
        }

        return ResponseEntity.noContent()
            .build();
    }

}
