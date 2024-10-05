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
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.models.viewmodels.training.TrainingWrite;
import springweb.training_manager.services.TrainingService;

import java.net.URI;

@RestController
@Secured({RoleSchema.ROLE_ADMIN, RoleSchema.ROLE_USER})
@RequestMapping(
    value = "/api/training",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class TrainingControllerAPI {
    private final TrainingService service;
    private static final Logger logger = LoggerFactory.getLogger(TrainingControllerAPI.class);

    @GetMapping()
    @ResponseBody
    ResponseEntity<Page<TrainingRead>> getAll(@PageableDefault(size = 2) Pageable page) {
        return ResponseEntity.ok(service.getAllAlternative(page));
    }

    @PostMapping()
    @ResponseBody
    ResponseEntity<TrainingRead> create(@RequestBody @Valid TrainingWrite toCreate) {
        Training created = service.create(toCreate, null);
        var trainingRead = new TrainingRead(created);
        return ResponseEntity.created(
            URI.create("/api/training/" + created.getId())
        ).body(trainingRead);
    }


}
