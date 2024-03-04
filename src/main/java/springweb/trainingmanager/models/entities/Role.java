package springweb.trainingmanager.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import springweb.trainingmanager.models.schemas.RoleSchema;

import java.util.Set;

@Entity
@Table(name = "roles")
public class Role extends RoleSchema {
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
    public Role() { }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
