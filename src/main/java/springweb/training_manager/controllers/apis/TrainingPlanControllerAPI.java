package springweb.training_manager.controllers.apis;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springweb.training_manager.controllers.with_views.TrainingPlanController;
import springweb.training_manager.models.entities.Weekdays;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.training_plan.TrainingPlansRead;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineReadIndex;
import springweb.training_manager.services.TrainingPlanService;
import springweb.training_manager.services.TrainingRoutineService;
import springweb.training_manager.services.UserService;

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
    private final Logger logger = LoggerFactory.getLogger(TrainingPlanController.class);

    @GetMapping
    public ResponseEntity<TrainingPlansRead> getAll(Authentication auth){
        var loggedUser = UserService.getUserByAuth(auth);
        List<TrainingRoutineReadIndex> plans = service.getAllByUser(loggedUser);
        return ResponseEntity.ok(
            new TrainingPlansRead(plans)
        );
    }
}
