package springweb.trainingmanager.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.Role;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.models.schemas.RoleSchema;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;
import springweb.trainingmanager.models.viewmodels.user.MyUserDetails;
import springweb.trainingmanager.models.viewmodels.user.UserWrite;
import springweb.trainingmanager.repositories.forcontrollers.RoleRepository;
import springweb.trainingmanager.repositories.forcontrollers.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final MyUserDetailsService userDetailsService;
    private final UserRepository repository;
    private final RoleService roleService;
    private final PasswordEncoder encoder;
    public static final String PASSWORDS_NOT_EQUAL_MESSAGE = "Hasła się różnią. Sprawdź poprawność haseł";

    public UserService(
        final MyUserDetailsService userDetailsService,
        final UserRepository repository,
        final RoleService roleService,
        final PasswordEncoder encoder
    ) {
        this.userDetailsService = userDetailsService;
        this.repository = repository;
        this.roleService = roleService;
        this.encoder = encoder;
    }

    /**
     * Method that gets authentication object, gets MyUserDetails out
     * of it and then returns its id.
     *
     * @param auth Authentication object
     *
     * @return If logged user is in role ROLE_USER then returns his id.
     *         Otherwise, returns null
     */
    public static String getUserIdByAuth(Authentication auth){
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        // TODO: Check if the user has role ADMIN. If yes, then return null too (just in case)
        return userDetails.isInRole(RoleSchema.ROLE_USER) ?
        userDetails.getUser().getId() :
        null;
    }

    /**
     * Method that gets authentication object, gets MyUserDetails out
     * of it and then returns user object.
     *
     * @param auth Authentication object
     *
     * @return Logged user object
     */
    public static User getUserByAuth(Authentication auth){
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }

    public boolean ifPasswordsMatches(String password, String passwordRepeat){
        return password.equals(passwordRepeat);
    }

    public void register(UserWrite toSave, Set<Role> roles){
        if(userDetailsService.userExists(toSave.getUsername()))
            throw new IllegalArgumentException("Istnieje już użytkownik o takiej nazwie. Może to Ty?");
        if(!ifPasswordsMatches(toSave.getPassword(), toSave.getPasswordRepeat()))
            throw new IllegalArgumentException(PASSWORDS_NOT_EQUAL_MESSAGE);

        Set<Role> rolesToSave = prepRoles(roles);

        User userToSave = toSave.toUser();
        userToSave.setPasswordHashed(encoder.encode(toSave.getPassword()));
        userToSave.setRoles(rolesToSave);

        rolesToSave.forEach(
            role -> {
                Set<User> users = role.getUsers();
                users.add(userToSave);

                role.setUsers(users);
                roleService.save(role);
            }
        );
        userDetailsService.createUser(new MyUserDetails(userToSave));

    }

    private Set<Role> prepRoles(Set<Role> roles){
        if(roles == null || roles.isEmpty())
            return null;

        Set<Role> rolesToSave = new HashSet<>(roles.size());
        roles.forEach(
            role -> {
                Role found = roleService.getOptionalRoleByName(role.getName())
                    .orElse(role);

                if(found.getId() == 0){
                    var savedRole = roleService.save(found);
                    rolesToSave.add(savedRole);
                }else
                    rolesToSave.add(found);

            }
        );
        return rolesToSave;
    }

}
