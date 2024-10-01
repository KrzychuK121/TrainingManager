package springweb.training_manager.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.RoleSchema;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends RoleSchema {
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
