package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springweb.training_manager.exceptions.user_service.SwitchToAdminException;
import springweb.training_manager.exceptions.user_service.UserNotFoundException;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.authentication.AuthResponse;
import springweb.training_manager.models.viewmodels.user.MyUserDetails;
import springweb.training_manager.models.viewmodels.user.UserCredentials;
import springweb.training_manager.models.viewmodels.user.UserRead;
import springweb.training_manager.models.viewmodels.user.UserWrite;
import springweb.training_manager.repositories.for_controllers.UserRepository;
import springweb.training_manager.security.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final MyUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public static final String PASSWORDS_NOT_EQUAL_MESSAGE = "Hasła się różnią. Sprawdź poprawność haseł";

    /**
     * Method that gets authentication object, gets MyUserDetails out of it and then
     * returns its id.
     *
     * @param auth Authentication object
     *
     * @return If logged user is in role ROLE_USER then returns his id. Otherwise, returns
     * null
     */
    public static String getUserIdByAuth(Authentication auth) {
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        // TODO: Check if the user has role ADMIN. If yes, then return null too (just in case)
        return userDetails.isInRole(Role.USER) ?
            userDetails.getUser()
                .getId() :
            null;
    }

    /**
     * Method that gets authentication object, gets MyUserDetails out of it and then
     * returns user object.
     *
     * @param auth Authentication object
     *
     * @return Logged user object
     */
    public static User getUserByAuth(Authentication auth) {
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }

    public static boolean userIsInRole(
        User user,
        Role role
    ) {
        return user.getRole()
            .equals(role);
    }

    public static boolean isAtLeastModerator(User requesting) {
        return userIsInRole(requesting, Role.ADMIN)
            || userIsInRole(requesting, Role.MODERATOR);
    }

    /**
     * This method checks logic if <code>requesting</code> can access resource that
     * contains reference to <code>owner</code> for edit/delete operations.
     *
     * @param requesting user (e.g. logged to app) that wants to access resource
     * @param owner      user that is an owner of requesting resource
     *
     * @return <strong>True</strong> when <strong>one</strong> of conditions are
     * fulfilled: <ul>
     * <li><code>Requesting</code> user is an administrator</li>
     * <li><code>Requesting</code> is equal to <code>owner</code></li>
     * </ul> Otherwise, returns <strong>false</strong>
     */
    public static boolean isPermittedToModifyFor(User requesting, User owner) {
        if (isAtLeastModerator(requesting))
            return true;
        return requesting.equals(owner);
    }

    /**
     * This method checks logic if <code>requesting</code> can access resource that
     * contains reference to <code>owner</code> for display/use in other entities
     * operations.
     *
     * @param requesting user (e.g. logged to app) that wants to access resource
     * @param owner      user that is an owner of requesting resource
     *
     * @return <strong>True</strong> when <strong>one</strong> of conditions are
     * fulfilled: <ul>
     * <li><code>Requesting</code> user can modify resource</li>
     * <li><code>Owner</code> is null (resource not owned by anybody - public)</li>
     * </ul> Otherwise, returns <strong>false</strong>
     *
     * @see #isPermittedToModifyFor(User, User)
     */
    public static boolean isPermittedToReadFor(User requesting, User owner) {
        return isPermittedToModifyFor(requesting, owner) || owner == null;
    }

    public Page<UserRead> getAllPagedUsers(Pageable page) {
        return PageSortService.getPageBy(
            User.class,
            page,
            repository::findAll,
            UserRead::new,
            log
        );
    }

    public boolean ifPasswordsMatches(String password, String passwordRepeat) {
        if (password == null || passwordRepeat == null)
            return false;
        return password.equals(passwordRepeat);
    }

    public void register(UserWrite toSave, Role role) {
        if (userDetailsService.userExists(toSave.getUsername()
            .toLowerCase()))
            throw new IllegalArgumentException("Istnieje już użytkownik o takiej nazwie. Może to Ty?");
        if (!ifPasswordsMatches(toSave.getPassword(), toSave.getPasswordRepeat()))
            throw new IllegalArgumentException(PASSWORDS_NOT_EQUAL_MESSAGE);

        toSave.setUsername(
            toSave.getUsername()
                .toLowerCase()
        );
        User userToSave = toSave.toEntity();
        userToSave.setPasswordHashed(encoder.encode(toSave.getPassword()));
        userToSave.setRole(role);
        userDetailsService.createUser(new MyUserDetails(userToSave));

    }

    public AuthResponse login(UserCredentials credentials) throws UsernameNotFoundException {
        UserDetails details = userDetailsService.loadUserByUsername(
            credentials.username()
                .toLowerCase()
        );
        MyUserDetails myDetails = (MyUserDetails) details;

        User foundUser = myDetails.getUser();
        var foundPassword = foundUser.getPasswordHashed();
        if (!encoder.matches(credentials.password(), foundPassword))
            return null;

        if (!myDetails.isAccountNonLocked())
            throw new LockedException("User is locked.");

        var token = jwtService.generateToken(details);

        return new AuthResponse(
            token,
            foundUser.getFirstName(),
            foundUser.getLastName(),
            foundUser.getRole()
        );
    }

    private User getUser(String userId) {
        return repository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }

    public void switchUsersRole(
        String userId,
        Role newRole
    ) {
        if (newRole == Role.ADMIN)
            throw new SwitchToAdminException();
        var user = getUser(userId);

        user.setRole(newRole);
        repository.save(user);
    }

    public void changeUserLockedStatus(
        String userIdToChange,
        boolean lock
    ) {
        var user = getUser(userIdToChange);
        if (userIsInRole(user, Role.ADMIN))
            throw new IllegalArgumentException("Cannot lock admin account.");

        user.setRole(Role.USER);
        user.setLocked(lock);
        repository.save(user);
    }
}
