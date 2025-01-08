package springweb.training_manager.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.UserSchema;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "identity_user")
public class User extends UserSchema {

    private String passwordHashed;


    public User(
        String firstName,
        String lastName,
        String username,
        String password
    ) {
        super(
            null,
            firstName,
            lastName,
            username,
            password,
            Role.USER
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;
        return passwordHashed.equals(user.passwordHashed) && super.equals(user);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + passwordHashed.hashCode();
        return result;
    }
}