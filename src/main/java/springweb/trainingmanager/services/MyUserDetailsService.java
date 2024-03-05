package springweb.trainingmanager.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.models.viewmodels.user.MyUserDetails;
import springweb.trainingmanager.repositories.forcontrollers.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsManager {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public MyUserDetailsService(
        final UserRepository repository,
        final PasswordEncoder encoder
    ) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User found = repository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

        return new MyUserDetails(found);
    }

    @Override
    public void createUser(UserDetails user) {
        User toSave = ((MyUserDetails) user).getUser();
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null)
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
