package springweb.training_manager.controllers.apis;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import springweb.training_manager.models.viewmodels.user.UserCredentials;
import springweb.training_manager.services.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserManagerControllerAPI {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserManagerControllerAPI.class);

    @PostMapping(
        value = "/login",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<?> login(@RequestBody UserCredentials userCredentials){
        try {
            var isValid = userService.login(userCredentials);
            return isValid
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(401).body("Wrong login or password.");
        } catch(UsernameNotFoundException ex) {
            return ResponseEntity.status(404).body("User " + userCredentials.username() + " does not exist.");
        }
    }
}