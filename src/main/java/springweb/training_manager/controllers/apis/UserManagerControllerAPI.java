package springweb.training_manager.controllers.apis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.viewmodels.authentication.AuthResponse;
import springweb.training_manager.models.viewmodels.authentication.NotValidRegister;
import springweb.training_manager.models.viewmodels.user.UserCredentials;
import springweb.training_manager.models.viewmodels.user.UserWrite;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.services.UserService;

import java.util.Set;

@RestController
@RequestMapping(
    value = "/api/auth",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class UserManagerControllerAPI {

    private final UserService userService;
    private final UserService service;
    private static final Logger logger = LoggerFactory.getLogger(UserManagerControllerAPI.class);

    @PostMapping("/login")
    @ResponseBody
    ResponseEntity<?> login(@RequestBody UserCredentials userCredentials) {
        try {
            var token = userService.login(userCredentials);
            return token != null
                ? ResponseEntity.ok(new AuthResponse(token))
                : ResponseEntity.status(401).body("Wrong login or password.");
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(404).body("User " + userCredentials.username() + " does not exist.");
        }
    }

    @PostMapping("/register")
    @ResponseBody
    ResponseEntity<?> register(
        @RequestBody @Valid UserWrite current,
        BindingResult result
    ) {
        if (!service.ifPasswordsMatches(current.getPassword(), current.getPasswordRepeat()))
            result.addError(new FieldError("current", "passwordRepeat", UserService.PASSWORDS_NOT_EQUAL_MESSAGE));
        
        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result);
            return ResponseEntity.badRequest().body(validation.getErrors());
        }

        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");

        try {
            service.register(current, Set.of(roleUser));
        } catch (IllegalArgumentException e) {
            logger.error("Exception occurs: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new NotValidRegister("User exists")
            );
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    @ResponseBody
    ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}
