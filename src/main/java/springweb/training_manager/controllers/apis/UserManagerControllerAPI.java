package springweb.training_manager.controllers.apis;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.exceptions.user_service.SwitchToAdminException;
import springweb.training_manager.exceptions.user_service.UserNotFoundException;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.viewmodels.authentication.NotValidRegister;
import springweb.training_manager.models.viewmodels.user.UserCredentials;
import springweb.training_manager.models.viewmodels.user.UserRead;
import springweb.training_manager.models.viewmodels.user.UserWrite;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.services.CaptchaService;
import springweb.training_manager.services.UserService;

import java.io.IOException;

@RestController
@RequestMapping(
    value = "/api/auth",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class UserManagerControllerAPI {
    private final CaptchaService captchaService;
    private final UserService service;
    private static final Logger logger = LoggerFactory.getLogger(UserManagerControllerAPI.class);

    @PostMapping("/login")
    @ResponseBody
    ResponseEntity<?> login(@RequestBody UserCredentials userCredentials) {
        try {
            var captchaVerified = captchaService.captchaVerified(userCredentials.captchaToken());
            if (!captchaVerified)
                return ResponseEntity.badRequest()
                    .build();
            var authResponse = service.login(userCredentials);
            return authResponse != null
                ? ResponseEntity.ok(authResponse)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Wrong login or password.");
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User " + userCredentials.username() + " does not exist.");
        } catch (LockedException ex) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                .build();
        } catch (IOException | InterruptedException ex) {
            return ResponseEntity.status(500)
                .body("Captcha service failed");
        }
    }

    @PostMapping("/register")
    @ResponseBody
    ResponseEntity<?> register(
        @RequestBody @Valid UserWrite current,
        BindingResult result
    ) throws IOException, InterruptedException {
        var captchaVerified = captchaService.captchaVerified(current.getCaptchaToken());
        if (!captchaVerified)
            return ResponseEntity.badRequest()
                .body(new NotValidRegister("Captcha verification failed."));
        if (!service.ifPasswordsMatches(current.getPassword(), current.getPasswordRepeat()))
            result.addError(new FieldError("current", "passwordRepeat", UserService.PASSWORDS_NOT_EQUAL_MESSAGE));

        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result);
            return ResponseEntity.badRequest()
                .body(validation.getErrors());
        }

        try {
            service.register(current, Role.USER);
        } catch (IllegalArgumentException e) {
            logger.error("Exception occurs: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new NotValidRegister("User exists"));
        }

        return ResponseEntity.noContent()
            .build();
    }

    @Secured(
        {
            Role.Constants.ADMIN,
            Role.Constants.MODERATOR,
            Role.Constants.USER
        }
    )
    @PostMapping("/logout")
    @ResponseBody
    ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent()
            .build();
    }

    @Secured(Role.Constants.ADMIN)
    @GetMapping("/all-users")
    @ResponseBody
    ResponseEntity<Page<UserRead>> getPagedUsers(@PageableDefault(size = 9) Pageable page) {
        var allPagedUsers = service.getAllPagedUsers(page);
        return ResponseEntity.ok(allPagedUsers);
    }

    @Secured(Role.Constants.ADMIN)
    @PatchMapping("/change-role/{userId}/{role}")
    @ResponseBody
    ResponseEntity<?> switchUserToRole(
        @PathVariable String userId,
        @PathVariable String role
    ) {
        Role mappedRole;
        try {
            mappedRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body("There is no such role: " + role);
        }
        try {
            service.switchUsersRole(userId, mappedRole);
            return ResponseEntity.noContent()
                .build();
        } catch (UserNotFoundException ex) {
            return ResponseEntity.notFound()
                .build();
        } catch (SwitchToAdminException ex) {
            return ResponseEntity.badRequest()
                .body(ex.getMessage());
        }
    }

    @Secured(Role.Constants.ADMIN)
    @PatchMapping("/change-user-lock/{userId}/{locked}")
    @ResponseBody
    ResponseEntity<?> changeUserLock(
        @PathVariable String userId,
        @PathVariable boolean locked
    ) {
        try {
            service.changeUserLockedStatus(userId, locked);
            return ResponseEntity.noContent()
                .build();
        } catch (UserNotFoundException ex) {
            return ResponseEntity.notFound()
                .build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .build();
        }
    }

    @Secured(Role.Constants.ADMIN)
    @Transactional
    @DeleteMapping("/{userId}")
    @ResponseBody
    ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return ResponseEntity.ok()
            .build();
    }
}
