package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.done_training.DoneTrainingWrite;
import springweb.training_manager.services.DoneTrainingService;
import springweb.training_manager.services.UserService;

@RestController
@Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
@RequestMapping(
    value = "/api/doneTrainings",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@Slf4j
public class DoneTrainingsRegisterAPI {
    private final DoneTrainingService service;

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> create(
        @RequestBody @Valid DoneTrainingWrite doneTrainingWrite,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        try {
            service.createOrEdit(doneTrainingWrite, loggedUser);
            return ResponseEntity.noContent()
                .build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(ex.getMessage());
        }
    }
}
