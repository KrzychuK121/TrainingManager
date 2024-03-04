package springweb.trainingmanager.repositories.forcontrollers;

import springweb.trainingmanager.models.entities.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
    Role save(Role entity);
    Optional<Role> findById(Integer integer);
    boolean existsById(Integer integer);
    void deleteById(Integer integer);
    void delete(Role entity);
}
