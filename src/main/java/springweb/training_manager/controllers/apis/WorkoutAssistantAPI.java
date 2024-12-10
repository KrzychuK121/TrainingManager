package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.viewmodels.workout_assistant.WorkoutAssistantWrite;
import springweb.training_manager.services.UserService;
import springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantService;

@RestController
@RequiredArgsConstructor
@RequestMapping(
    value = "/api/assistant",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Slf4j
public class WorkoutAssistantAPI {
    private final WorkoutAssistantService service;

    @PostMapping
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

        log.info("validated data: {}", data);
        var user = UserService.getUserByAuth(auth);
        service.planTrainingRoutine(
            data,
            user
        );

        return ResponseEntity.noContent()
            .build();
    }
}
