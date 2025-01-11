package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.viewmodels.workout_assistant.WorkoutAssistantWrite;
import springweb.training_manager.services.UserService;
import springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantService;
import springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantTypedServices.WAMuscleGrowService;
import springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantTypedServices.WATypedBase;
import springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantTypedServices.WAWeightReductionService;

@RestController
@Secured({Role.Constants.USER})
@RequiredArgsConstructor
@RequestMapping(
    value = "/api/assistant",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Slf4j
public class WorkoutAssistantAPI {
    private final WorkoutAssistantService service;

    private final WAMuscleGrowService muscleGrowService;
    private final WAWeightReductionService weightReductionService;

    private WATypedBase getWATypedService(WorkoutAssistantWrite data) {
        switch (data.getWorkoutType()) {
            case WEIGHT_REDUCTION -> {
                return weightReductionService;
            }
            case MUSCLE_GROW -> {
                return muscleGrowService;
            }
        }
        log.error("WorkoutAssistantWrite data is incorrect. Can't decide which service to choose.");
        throw new IllegalStateException("WorkoutAssistantWrite data is incorrect. Can't decide which service to choose.");
    }

    @PostMapping("/create-plan") // Create and don't save in database
    @ResponseBody
    public ResponseEntity<?> planTraining(
        @RequestBody @Valid WorkoutAssistantWrite data,
        BindingResult result,
        Authentication auth
    ) {
        var validationErrors = service.validateAndPrepare(data, result);
        if (validationErrors != null)
            return ResponseEntity.badRequest()
                .body(validationErrors.getErrors());

        var user = UserService.getUserByAuth(auth);
        return ResponseEntity.ok(
            service.planTrainingRoutine(
                data,
                getWATypedService(data),
                user
            )
        );
    }

    @PostMapping("/save-plan") // Save created in database
    @ResponseBody
    public ResponseEntity<?> getPlannedRoutine(
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        try {
            service.savePlannedRoutine(loggedUser);
            return ResponseEntity.noContent()
                .build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.notFound()
                .build();
        }
    }
}
