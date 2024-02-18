package springweb.trainingmanager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.trainingmanager.models.entities.Role;
import springweb.trainingmanager.repositories.forcontrollers.RoleRepository;

import java.util.Optional;

@Repository
interface SqlRoleRepository extends RoleRepository, JpaRepository<Role, Integer> {
    @Override
    Optional<Role> findByName(String name);
}
