package springweb.trainingmanager.models.schemas;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@MappedSuperclass
public abstract class UserSchema {
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
    protected String username;

    @Length(min = 8, max = 30, message = "Hasło musi mieć od 8 do 30 znaków")
    protected String password;

    public UserSchema() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
