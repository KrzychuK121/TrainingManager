package springweb.training_manager.services;

import org.springframework.data.jpa.repository.JpaRepository;
import springweb.training_manager.models.schemas.Identificable;
import springweb.training_manager.models.viewmodels.Castable;
import springweb.training_manager.repositories.for_controllers.DuplicationRepository;
import springweb.training_manager.repositories.for_controllers.Saveable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NoDuplicationService {

    /**
     * This method is used to find existing E entity in database or create new one.
     * After that, it is returned to caller. Can be used e.g. in <code>create</code> method in services
     * where object has relation many to one or in <code>NoDuplicationService</code> methods
     *
     * @param <I>        type of identifier (id)
     * @param <E>        type of entity saved in database
     * @param <R>        type of repository that finds duplicates of E entity
     * @param entity     Entity of type E.
     * @param repository repository that extends <code>DuplicationRepository</code> and is working for E entity type
     * @param savable    interface that provides save operation for E entity
     * @return prepared entity (founded in database or just created)
     */
    public static <
        I,
        E extends Identificable<I>,
        R extends DuplicationRepository<E>
        > E prepEntity(
        E entity,
        R repository,
        Saveable<E> savable
    ) {
        if (entity == null)
            return null;

        E found = repository.findDuplication(entity)
            .orElse(entity);

        if (found.getId() != found.defaultId())
            return found;

        return savable.save(found);
    }

    /**
     * This method is used to find existing E entities in database or create new one
     * corresponding to objects inside <code>entities</code>. After that, they are
     * returned as a new list. Can be used e.g. in <code>create</code> method in services
     * where object has relation many to one
     *
     * @param <I>        type of identifier (id)
     * @param <E>        type of entity saved in database
     * @param <R>        type of repository that finds duplicates of E entity
     * @param entities   list of entities of type E
     * @param repository repository that extends <code>DuplicationRepository</code> and is working for E entity type
     * @param savable    interface that provides save operation for E entity
     * @return prepared list of entities (founded in database or just created)
     */
    public static <
        I,
        E extends Identificable<I>,
        R extends JpaRepository<E, ?> & DuplicationRepository<E>
        > List<E> prepEntities(
        List<E> entities,
        R repository,
        Saveable<E> savable
    ) {
        if (entities == null || entities.isEmpty())
            return null;

        List<E> preparedEntities = new ArrayList<>(entities.size());

        entities.forEach(
            writeModel -> prepEntity(writeModel, repository, savable)
        );

        return preparedEntities;
    }

    public static <
        I,
        E extends Identificable<I>,
        W extends Castable<E>,
        R extends JpaRepository<E, ?> & DuplicationRepository<E>
        > List<E> prepEntitiesWithWriteModel(
        List<W> writeModels,
        R repository,
        Saveable<E> savable
    ) {
        return prepEntities(
            writeModels.stream()
                .map(Castable::toEntity)
                .collect(Collectors.toList()),
            repository,
            savable
        );
    }


}
