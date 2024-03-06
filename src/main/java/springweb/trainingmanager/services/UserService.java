package springweb.trainingmanager.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.Role;
import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.models.viewmodels.user.MyUserDetails;
import springweb.trainingmanager.models.viewmodels.user.UserWrite;
import springweb.trainingmanager.repositories.forcontrollers.RoleRepository;
import springweb.trainingmanager.repositories.forcontrollers.UserRepository;

import java.util.Set;

@Service
public class UserService {
    private final MyUserDetailsService userDetailsService;
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    public static final String PASSWORDS_NOT_EQUAL_MESSAGE = "Hasła się różnią. Sprawdź poprawność haseł";

    public UserService(
        final MyUserDetailsService userDetailsService,
        final UserRepository repository,
        final RoleRepository roleRepository,
        final PasswordEncoder encoder
    ) {
        this.userDetailsService = userDetailsService;
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    public boolean ifPasswordsMatches(String password, String passwordRepeat){
        return password.equals(passwordRepeat);
    }

    public void register(UserWrite toSave, Set<Role> roles){
        if(userDetailsService.userExists(toSave.getUsername()))
            throw new IllegalArgumentException("Istnieje już użytkownik o takiej nazwie. Może to Ty?");
        if(!ifPasswordsMatches(toSave.getPassword(), toSave.getPasswordRepeat()))
            throw new IllegalArgumentException(PASSWORDS_NOT_EQUAL_MESSAGE);

        User userToSave = toSave.toUser();
        userToSave.setPasswordHashed(encoder.encode(toSave.getPassword()));
        userToSave.setRoles(roles);

        roles.forEach(
            role -> {
                Set<User> users = role.getUsers();
                users.add(userToSave);

                role.setUsers(users);
                roleRepository.save(role);
            }
        );
        userDetailsService.createUser(new MyUserDetails(userToSave));
        //User saved = ((MyUserDetails) userDetailsService.loadUserByUsername(toSave.getUsername())).getUser();

    }

}
