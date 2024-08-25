package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.repositories.for_controllers.RoleRepository;

import java.util.Optional;

@Repository
interface SqlRoleRepository extends RoleRepository, JpaRepository<Role, Integer> {
    @Override
    Optional<Role> findByName(String name);
}
