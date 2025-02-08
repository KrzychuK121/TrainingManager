package springweb.training_manager.models.view_models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.entities.User;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class MyUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(user.getRole());
    }

    public boolean isInRole(Role role) {
        return getAuthorities()
            .stream()
            .anyMatch(
                grantedAuthority -> grantedAuthority.getAuthority()
                    .equals(role.name())
            );
    }

    @Override
    public String getPassword() {
        return user.getPasswordHashed();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
