package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.UserSchema;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "identity_user")
public class User extends UserSchema {

    private String passwordHashed;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    @ManyToMany
    @JoinTable(
        name = "users_exercises",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    )
    private Set<Exercise> exercises = new HashSet<>();

    public User(
        String firstName,
        String lastName,
        String username,
        String password
    ) {
        super(
            "",
            firstName,
            lastName,
            username,
            password
        );
    }
}