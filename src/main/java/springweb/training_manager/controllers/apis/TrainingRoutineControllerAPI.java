package springweb.training_manager.controllers.apis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.controllers.web_sockets.TrainingPlanControllerWS;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineRead;
import springweb.training_manager.services.TimerServices.NotificationTimerService;
import springweb.training_manager.services.TrainingRoutineService;
import springweb.training_manager.services.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
@RequestMapping(
    value = "/api/routines",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@Slf4j
public class TrainingRoutineControllerAPI {
    private final TrainingRoutineService service;
    private final NotificationTimerService notificationTimerService;
    private final SimpMessageSendingOperations messageTemplate;

    @GetMapping
    public ResponseEntity<List<TrainingRoutineRead>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> switchActive(
        @PathVariable int id,
        Authentication auth
    ) {
        var user = UserService.getUserByAuth(auth);
        try {
            service.switchActive(id, user);

            var newReminder = notificationTimerService.getReminderAndInitRest(auth);
            if (newReminder != null)
                messageTemplate.convertAndSendToUser(
                    auth.getName(),
                    TrainingPlanControllerWS.NOTIFICATION_ENDPOINT,
                    newReminder
                );
            else
                notificationTimerService.cancelTimerForUser(auth);
            return ResponseEntity.noContent()
                .build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                .build();
        }
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
