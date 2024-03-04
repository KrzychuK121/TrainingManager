package springweb.trainingmanager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.repositories.forcontrollers.UserRepository;

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
