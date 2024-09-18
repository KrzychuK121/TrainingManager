package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.user.MyUserDetails;
import springweb.training_manager.models.viewmodels.user.UserCredentials;
import springweb.training_manager.models.viewmodels.user.UserWrite;
import springweb.training_manager.repositories.for_controllers.UserRepository;
import springweb.training_manager.security.JwtService;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MyUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository repository;
    private final RoleService roleService;
    private final PasswordEncoder encoder;

    public static final String PASSWORDS_NOT_EQUAL_MESSAGE = "Hasła się różnią. Sprawdź poprawność haseł";


    /**
     * Method that gets authentication object, gets MyUserDetails out
     * of it and then returns its id.
     *
     * @param auth Authentication object
     * @return If logged user is in role ROLE_USER then returns his id.
     * Otherwise, returns null
     */
    public static String getUserIdByAuth(Authentication auth) {
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
     * @return Logged user object
     */
    public static User getUserByAuth(Authentication auth) {
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }

    public boolean ifPasswordsMatches(String password, String passwordRepeat) {
        return password.equals(passwordRepeat);
    }

    public void register(UserWrite toSave, Set<Role> roles) {
        if (userDetailsService.userExists(toSave.getUsername().toLowerCase()))
            throw new IllegalArgumentException("Istnieje już użytkownik o takiej nazwie. Może to Ty?");
        if (!ifPasswordsMatches(toSave.getPassword(), toSave.getPasswordRepeat()))
            throw new IllegalArgumentException(PASSWORDS_NOT_EQUAL_MESSAGE);

        Set<Role> rolesToSave = prepRoles(roles);

        toSave.setUsername(toSave.getUsername().toLowerCase());
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

    public String login(UserCredentials credentials) throws UsernameNotFoundException {
        UserDetails details = userDetailsService.loadUserByUsername(
            credentials.username().toLowerCase()
        );
        User foundUser = ((MyUserDetails) details).getUser();
        var foundPassword = foundUser.getPasswordHashed();
        if (!encoder.matches(credentials.password(), foundPassword))
            return null;

        return jwtService.generateToken(details);
    }

    private Set<Role> prepRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty())
            return null;

        Set<Role> rolesToSave = new HashSet<>(roles.size());
        roles.forEach(
            role -> {
                Role found = roleService.getOptionalRoleByName(role.getName())
                    .orElse(role);

                if (found.getId() == 0) {
                    var savedRole = roleService.save(found);
                    rolesToSave.add(savedRole);
                } else
                    rolesToSave.add(found);

            }
        );
        return rolesToSave;
    }

}
