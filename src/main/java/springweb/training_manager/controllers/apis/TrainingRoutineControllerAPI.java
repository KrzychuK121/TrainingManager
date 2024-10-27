package springweb.training_manager.controllers.apis;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineRead;
import springweb.training_manager.services.TrainingRoutineService;
import springweb.training_manager.services.UserService;

import java.util.List;

@RestController
@Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
@RequestMapping(
    value = "/api/routines",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class TrainingRoutineControllerAPI {
    private final TrainingRoutineService service;
    private static final Logger logger = LoggerFactory.getLogger(TrainingRoutineControllerAPI.class);

    @GetMapping
    public ResponseEntity<List<TrainingRoutineRead>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(
        @PathVariable int id,
        Authentication auth
    ) {
        var userId = UserService.getUserIdByAuth(auth);
        try {
            service.deleteById(id, userId);
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
