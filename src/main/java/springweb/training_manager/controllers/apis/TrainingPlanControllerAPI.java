package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.models.viewmodels.training_plan.TrainingPlansRead;
import springweb.training_manager.models.viewmodels.training_plan.TrainingPlansWrite;
import springweb.training_manager.models.viewmodels.training_plan.TrainingReminderRead;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineReadIndex;
import springweb.training_manager.models.viewmodels.training_schedule.TrainingScheduleRead;
import springweb.training_manager.services.TrainingPlanService;
import springweb.training_manager.services.TrainingRoutineService;
import springweb.training_manager.services.UserService;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(
    value = "/api/plans",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
public class TrainingPlanControllerAPI {
    private final TrainingPlanService service;
    private final TrainingRoutineService routineService;
    private final Logger logger = LoggerFactory.getLogger(TrainingPlanControllerAPI.class);

    @GetMapping
    public ResponseEntity<TrainingPlansRead> getAll(Authentication auth) {
        var loggedUser = UserService.getUserByAuth(auth);
        List<TrainingRoutineReadIndex> plans = service.getAllByUser(loggedUser);
        return ResponseEntity.ok(
            new TrainingPlansRead(plans)
        );
    }

    @GetMapping("/week")
    public ResponseEntity<TrainingPlansRead> getWeek(Authentication auth) {
        // TODO: Extract it to service
        String userId = UserService.getUserIdByAuth(auth);
        try {
            List<TrainingPlan> activePlans = service.getUserActivePlans(userId);
            var routineId = activePlans.get(0).getTrainingRoutineId();
            var readModel = new TrainingRoutineReadIndex(routineId, true);
            activePlans.forEach(
                trainingPlan -> readModel.putSchedule(
                    trainingPlan.getTrainingSchedule().getWeekday(),
                    new TrainingScheduleRead(
                        trainingPlan.getTrainingSchedule()
                    )
                )
            );
            List<TrainingRoutineReadIndex> mappedData = List.of(readModel);
            return ResponseEntity.ok(
                new TrainingPlansRead(
                    mappedData
                )
            );
        } catch (IllegalStateException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/today-training")
    @ResponseBody
    public ResponseEntity<TrainingRead> getTodayTraining(Authentication auth) {
        var userId = UserService.getUserIdByAuth(auth);
        var training = service.getUserActiveTraining(userId);
        if (training == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(training);
    }

    @GetMapping("/today-training-reminder")
    @ResponseBody
    public ResponseEntity<TrainingReminderRead> getTodayTrainingReminder(Authentication auth) {
        var userId = UserService.getUserIdByAuth(auth);
        var trainingReminder = service.getUserTrainingReminder(userId);
        if (trainingReminder == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(trainingReminder);
    }

    @GetMapping("/id")
    public ResponseEntity<TrainingPlansRead> getById(
        @PathVariable int id,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        List<TrainingRoutineReadIndex> plans = service.getAllByUser(loggedUser);
        return ResponseEntity.ok(
            new TrainingPlansRead(plans)
        );
    }

    @PostMapping
    public ResponseEntity<?> create(
        @RequestBody @Valid TrainingPlansWrite schedulesList,
        BindingResult result,
        Authentication auth
    ) {
        try {
            List<TrainingPlan> created = service.createNewPlans(
                schedulesList,
                UserService.getUserByAuth(auth)
            );

            return ResponseEntity.created(
                URI.create("/api/plans/" + created.get(0).getTrainingRoutine().getId())
            ).body(service.getMapFromPlans(created));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
