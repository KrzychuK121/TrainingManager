package springweb.training_manager.models.schemas;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import springweb.training_manager.models.entities.Role;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class UserSchema implements Identificable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;
    @NotBlank(message = "Imie użytkownika nie może być puste.")
    @Length(max = 25)
    protected String firstName;
    @NotBlank(message = "Nazwisko użytkownika nie może być puste.")
    @Length(max = 30)
    protected String lastName;
    @Column(nullable = false, unique = true)
    @Length(min = 8, max = 20, message = "Nazwa użytkownika musi mieć od 8 do 20 znaków.")
    protected String username;
    @Transient
    @Length(min = 8, max = 30, message = "Hasło musi mieć od 8 do 30 znaków.")
    protected String password;
    @NotNull(message = "Status zablokowania konta jest wymagany")
    protected boolean locked = false;
    @NotNull(message = "Rola jest wymagana")
    @Enumerated(EnumType.STRING)
    protected Role role;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String defaultId() {
        return "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSchema that = (UserSchema) o;
        return id.equals(that.id)
            && firstName.equals(that.firstName)
            && lastName.equals(that.lastName)
            && username.equals(that.username)
            && role.equals(that.role);
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getFirstName().hashCode();
        result = 31 * result + getLastName().hashCode();
        result = 31 * result + getUsername().hashCode();
        result = 31 * result + Objects.hashCode(getPassword());
        result = 31 * result + Boolean.hashCode(isLocked());
        result = 31 * result + getRole().hashCode();
        return result;
    }
}
