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
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.view_models.training.WorkoutTrainingRead;
import springweb.training_manager.models.view_models.training_plan.TrainingPlansEditRead;
import springweb.training_manager.models.view_models.training_plan.TrainingPlansPagedRead;
import springweb.training_manager.models.view_models.training_plan.TrainingPlansRead;
import springweb.training_manager.models.view_models.training_plan.TrainingPlansWrite;
import springweb.training_manager.models.view_models.training_routine.TrainingRoutineReadIndex;
import springweb.training_manager.models.view_models.training_schedule.TrainingScheduleRead;
import springweb.training_manager.models.view_models.validation.ValidationErrors;
import springweb.training_manager.repositories.for_controllers.DoneTrainingRepository;
import springweb.training_manager.services.TrainingPlanService;
import springweb.training_manager.services.UserService;

import java.net.URI;
import java.time.LocalDate;
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
    value = "/api/plans",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class TrainingPlanControllerAPI {
    private final TrainingPlanService service;
    private final DoneTrainingRepository doneTrainingRepository;
    private final Logger logger = LoggerFactory.getLogger(TrainingPlanControllerAPI.class);

    @GetMapping
    public ResponseEntity<TrainingPlansPagedRead> getAll(
        @PageableDefault(size = 5) Pageable page,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        Page<TrainingRoutineReadIndex> plans = service.getPagedForUser(loggedUser, page);
        return ResponseEntity.ok(
            new TrainingPlansPagedRead(plans)
        );
    }

    @Secured({Role.Constants.USER})
    @GetMapping("/week")
    public ResponseEntity<TrainingPlansRead> getWeek(Authentication auth) {
        // TODO: Extract it to service
        User user = UserService.getUserByAuth(auth);
        try {
            List<TrainingPlan> activePlans = service.getUserActivePlans(user.getId());
            var routineId = activePlans.get(0)
                .getTrainingRoutineId();
            var readModel = new TrainingRoutineReadIndex(
                routineId,
                true,
                user
            );
            activePlans.forEach(
                trainingPlan -> readModel.putSchedule(
                    trainingPlan.getTrainingSchedule()
                        .getWeekday(),
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
            return ResponseEntity.notFound()
                .build();
        }
    }

    @Secured({Role.Constants.USER})
    @GetMapping("/today-training")
    @ResponseBody
    public ResponseEntity<WorkoutTrainingRead> getTodayTraining(Authentication auth) {
        var userId = UserService.getUserIdByAuth(auth);
        try {
            var training = service.getUserActiveTraining(userId);
            if (training == null)
                return ResponseEntity.notFound()
                    .build();
            var todayDate = LocalDate.now();
            if (
                doneTrainingRepository.existsForOwnerForDate(
                    userId,
                    todayDate
                )
            )
                return ResponseEntity.noContent()
                    .build();
            return ResponseEntity.ok(training);
        } catch (IllegalStateException ex) {
            return ResponseEntity.notFound()
                .build();
        }
    }

    @GetMapping("/editModel/{id}")
    @ResponseBody
    public ResponseEntity<TrainingPlansEditRead> getEditMode(
        @PathVariable int id,
        Authentication auth
    ) {
        try {
            var userId = UserService.getUserIdByAuth(auth);
            TrainingPlansEditRead plans = service.getUserEditPlans(
                userId,
                id
            );

            return ResponseEntity.ok(
                plans
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .build();
        }
    }


    @PostMapping
    public ResponseEntity<?> create(
        @RequestBody @Valid TrainingPlansWrite schedulesList,
        BindingResult result,
        Authentication auth
    ) {
        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result, "planWriteMap.");
            return ResponseEntity.badRequest()
                .body(
                    validation.getErrors()
                );
        }

        try {
            List<TrainingPlan> created = service.createNewPlans(
                schedulesList,
                UserService.getUserByAuth(auth)
            );

            return ResponseEntity.created(
                    URI.create("/api/plans/" + created.get(0)
                        .getTrainingRoutine()
                        .getId())
                )
                .body(service.getMapFromPlans(created));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest()
                .build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(
        @PathVariable int id,
        @RequestBody @Valid TrainingPlansWrite schedulesList,
        BindingResult result,
        Authentication auth
    ) {
        var user = UserService.getUserByAuth(auth);
        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result, "planWriteMap.");
            return ResponseEntity.badRequest()
                .body(
                    validation.getErrors()
                );
        }

        try {
            service.edit(schedulesList, id, user);
        } catch (IllegalArgumentException e) {
            logger.error("Wystąpił wyjątek: " + e.getMessage());
            return ResponseEntity.notFound()
                .build();
        }

        return ResponseEntity.noContent()
            .build();
    }
}
