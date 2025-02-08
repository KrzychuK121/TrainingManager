package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.view_models.user_request.UserRequestRead;
import springweb.training_manager.models.view_models.user_request.UserRequestWrite;
import springweb.training_manager.services.UserRequestService;
import springweb.training_manager.services.UserService;

@RestController
@RequestMapping(
    value = "/api/user-request",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class UserRequestControllerAPI {
    private final UserRequestService service;

    @Secured(
        {
            Role.Constants.ADMIN,
            Role.Constants.MODERATOR,
            Role.Constants.USER
        }
    )
    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<UserRequestRead>> getAll(
        Pageable page,
        Authentication auth
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        return ResponseEntity.ok(
            service.getAllPaged(page, loggedUser)
        );
    }

    @Secured(Role.Constants.USER)
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> create(
        @RequestBody @Valid UserRequestWrite userRequestWrite,
        BindingResult result,
        Authentication auth
    ) {
        var validation = service.validateAndPrepare(
            userRequestWrite,
            result
        );

        if (validation != null)
            return ResponseEntity.badRequest()
                .body(validation);

        var loggedUser = UserService.getUserByAuth(auth);
        try {
            service.create(
                userRequestWrite,
                loggedUser
            );
            return ResponseEntity.noContent()
                .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .build();
        }
    }

    @Secured(
        {
            Role.Constants.ADMIN,
            Role.Constants.MODERATOR
        }
    )
    @PatchMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> closeRequest(
        Authentication auth,
        @PathVariable int id
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        try {
            service.closeRequest(id, loggedUser);
            return ResponseEntity.noContent()
                .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound()
                .build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .build();
        }
    }

    @Secured(
        {
            Role.Constants.ADMIN,
            Role.Constants.MODERATOR,
            Role.Constants.USER
        }
    )
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(
        Authentication auth,
        @PathVariable int id
    ) {
        var loggedUser = UserService.getUserByAuth(auth);
        try {
            service.delete(id, loggedUser);
            return ResponseEntity.noContent()
                .build();
        } catch (IllegalArgumentException e) {
            if (
                e.getMessage()
                    .contains("does not exist")
            )
                return ResponseEntity.notFound()
                    .build();

            return ResponseEntity.badRequest()
                .body("You are not allowed to delete this request");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body("You can't delete closed request");
        }
    }
}
