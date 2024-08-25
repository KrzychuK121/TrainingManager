package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.repositories.for_controllers.UserRepository;

import java.util.Optional;

@Repository
interface SqlUserRepository extends UserRepository, JpaRepository<User, String> {
    @Override
    Optional<User> findByUsername(String username);

    @Override
    boolean existsByUsername(String username);
    @Override
    void deleteByUsername(String username);
}
