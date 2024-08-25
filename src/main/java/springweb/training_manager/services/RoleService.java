package springweb.training_manager.services;

import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.repositories.for_controllers.RoleRepository;

import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository repository;

    public RoleService(
        final RoleRepository repository
    ) {
        this.repository = repository;
    }

    public Role save(Role toSave){
        return repository.save(toSave);
    }

    public Role getRoleByName(String name){
        return repository.findByName(name)
        .orElseThrow(() -> new IllegalArgumentException("Rola o podanej nazwie nie istnieje."));
    }

    public Optional<Role> getOptionalRoleByName(String name){
        return repository.findByName(name);
    }
}
