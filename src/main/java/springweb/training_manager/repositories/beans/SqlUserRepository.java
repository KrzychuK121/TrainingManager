package springweb.training_manager.repositories.beans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.repositories.for_controllers.UserRepository;

import java.util.Optional;

@Repository
interface SqlUserRepository extends UserRepository, JpaRepository<User, String> {
    @Override
    Optional<User> findByUsername(String username);

    @Override
    @Query("""
            SELECT u FROM User u
            WHERE u.role != springweb.training_manager.models.entities.Role.ADMIN
        """)
    Page<User> findAll(Pageable pageable);

    @Override
    boolean existsByUsername(String username);

    @Override
    void deleteByUsername(String username);

    @Override
    @Procedure("make_resources_public_for")
    void make_resources_public_for(String owner_id_to_clear);
}
