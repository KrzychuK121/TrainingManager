package springweb.trainingmanager.services;

import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.viewmodels.user.MyUserDetails;
import springweb.trainingmanager.models.viewmodels.user.UserWrite;
import springweb.trainingmanager.repositories.forcontrollers.UserRepository;

@Service
public class UserService {
    private final MyUserDetailsService userDetailsService;
    private final UserRepository repository;
    public static final String PASSWORDS_NOT_EQUAL_MESSAGE = "Hasła się różnią. Sprawdź poprawność haseł";

    public UserService(
        final MyUserDetailsService userDetailsService,
        final UserRepository repository
    ) {
        this.userDetailsService = userDetailsService;
        this.repository = repository;
    }

    public boolean ifPasswordsMatches(String password, String passwordRepeat){
        return password.equals(passwordRepeat);
    }

    public void register(UserWrite toSave){
        if(userDetailsService.userExists(toSave.getUsername()))
            throw new IllegalArgumentException("Istnieje już użytkownik o takiej nazwie. Może to Ty?");
        if(!ifPasswordsMatches(toSave.getPassword(), toSave.getPasswordRepeat()))
            throw new IllegalArgumentException(PASSWORDS_NOT_EQUAL_MESSAGE);

        userDetailsService.createUser(new MyUserDetails(toSave.toUser()));
    }

}
