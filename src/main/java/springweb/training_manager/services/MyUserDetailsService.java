package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.view_models.user.MyUserDetails;
import springweb.training_manager.repositories.for_controllers.UserRepository;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsManager {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User found = repository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

        return new MyUserDetails(found);
    }

    @Override
    public void createUser(UserDetails user) {
        User toSave = ((MyUserDetails) user).getUser();

        toSave.setFirstName(
            toSave.getFirstName()
                .toLowerCase()
        );
        toSave.setLastName(
            toSave.getLastName()
                .toLowerCase()
        );

        repository.save(toSave);
    }

    @Override
    public void updateUser(UserDetails user) {
        User toUpdate = ((MyUserDetails) user).getUser();
        repository.save(toUpdate);
    }

    @Override
    public void deleteUser(String username) {
        repository.deleteByUsername(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();
        if (auth == null)
            throw new IllegalStateException("Nie można zmienić hasła ponieważ użytkownik nie jest zalogowany.");

        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        User userToUpdate = userDetails.getUser();

        userToUpdate.setPassword(newPassword);
        userToUpdate.setPasswordHashed(encoder.encode(newPassword));

        repository.save(userToUpdate);
    }

    @Override
    public boolean userExists(String username) {
        return repository.existsByUsername(username);
    }
}
