package springweb.training_manager.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springweb.training_manager.models.schemas.Identificable;
import springweb.training_manager.models.viewmodels.Castable;
import springweb.training_manager.repositories.for_controllers.DuplicationRepository;
import springweb.training_manager.repositories.for_controllers.Saveable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoDuplicationServiceTest {
    private DuplicationRepository<MyEntity> repository;
    private Saveable<MyEntity> save;

    @Setter
    @Builder
    static class MyEntityWrite implements Castable<MyEntity> {
        private String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyEntityWrite myEntity = (MyEntityWrite) o;
            return Objects.equals(name, myEntity.name);
        }

        @Override
        public MyEntity toEntity() {
            return new MyEntity(null, name);
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    static class MyEntity implements Identificable<Integer> {
        private Integer id;
        private String name;

        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public Integer defaultId() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyEntity myEntity = (MyEntity) o;
            return Objects.equals(id, myEntity.id)
                && Objects.equals(name, myEntity.name);
        }
    }

    @BeforeEach
    void setup() {
        repository = mock(DuplicationRepository.class);
        save = mock(Saveable.class);
    }

    @Test
    void prepEntity_returned_null_when_entity_null() {
        // Given

        // When
        Object found = NoDuplicationService.prepEntity(
            null,
            repository,
            save
        );

        // Then
        assertNull(found, "Found should be null because provided null value.");
        verify(repository, never()).findDuplication(any());
        verify(save, never()).save(any());
    }

    @Test
    void prepEntity_returned_existing_entity() {
        // Given
        final var ID = 1;
        final var NAME = "fooName";

        var existingEntity = new MyEntity(ID, NAME);
        var toBeFound = new MyEntity(null, NAME);

        when(repository.findDuplication(any()))
            .thenReturn(Optional.of(existingEntity));

        // When
        MyEntity found = NoDuplicationService.prepEntity(
            toBeFound,
            repository,
            save
        );

        // Then
        assertEquals(found, existingEntity, "Found should be the same as existing one.");
        verify(repository).findDuplication(toBeFound);
        verify(save, never()).save(any());
    }

    @Test
    void prepEntity_created_new_entity_and_returned_id() {
        // Given
        final var TO_CREATE_NAME = "anotherName";
        final var NEW_CREATED_ID = 2;
        var toCreate = new MyEntity(null, TO_CREATE_NAME);
        var expectedCreated = new MyEntity(NEW_CREATED_ID, TO_CREATE_NAME);

        when(repository.findDuplication(toCreate))
            .thenReturn(Optional.empty());
        when(save.save(toCreate))
            .thenReturn(expectedCreated);

        // When
        var shouldBeCreated = NoDuplicationService.prepEntity(
            toCreate,
            repository,
            save
        );

        // Then
        assertEquals(shouldBeCreated, expectedCreated, "Entity should be created, not fetched");
        verify(repository).findDuplication(toCreate);
        verify(save).save(toCreate);
    }

    @Test
    void prepEntities_returns_null_when_provided_null_() {
        // Given

        // When
        var found = NoDuplicationService.prepEntities(
            null,
            repository,
            save
        );

        // Then
        assertNull(found, "Found should be null because provided null value.");
        verify(repository, never()).findDuplication(any());
        verify(save, never()).save((any()));
    }

    @Test
    void prepEntities_returns_null_when_provided_empty() {
        // Given

        // When
        var found = NoDuplicationService.prepEntities(
            List.of(),
            repository,
            save
        );

        // Then
        assertNull(found, "Found should be null because provided empty list.");
        verify(repository, never()).findDuplication(any());
        verify(save, never()).save((any()));
    }

    @Test
    void prepEntities_returns_list_of_found_entities() {
        // Given
        final var FIRST_ID = 1;
        final var FIRST_NAME = "foo1";
        final var SECOND_ID = 2;
        final var SECOND_NAME = "foo2";

        var existingEntity1 = new MyEntity(FIRST_ID, FIRST_NAME);
        var existingEntity2 = new MyEntity(SECOND_ID, SECOND_NAME);
        var existingList = List.of(existingEntity1, existingEntity2);

        var toFind1 = new MyEntity(null, FIRST_NAME);
        var toFind2 = new MyEntity(null, SECOND_NAME);

        when(repository.findDuplication(toFind1))
            .thenReturn(Optional.of(existingEntity1));
        when(repository.findDuplication(toFind2))
            .thenReturn(Optional.of(existingEntity2));

        // When
        var foundList = NoDuplicationService.prepEntities(
            List.of(toFind1, toFind2),
            repository,
            save
        );

        // Then
        assertNotNull(foundList, "Found list should not be null");
        for (int i = 0; i < existingList.size(); i++) {
            var found = foundList.get(i);
            var existing = existingList.get(i);

            assertEquals(
                found,
                existing,
                String.format(
                    "Found (id: %d, name: %s) should contains the same values as Existing (id: %d, name: %s)",
                    found.getId(),
                    found.getName(),
                    existing.getId(),
                    existing.getName()
                )
            );
        }

        verify(repository, times(2)).findDuplication(any());
        verify(save, never()).save(any());
    }

    @Test
    void prepEntities_returns_list_of_created_entities() {
        // Given
        final var FIRST_NAME = "foo1";
        final var SECOND_NAME = "foo2";

        var toCreate1 = new MyEntity(null, FIRST_NAME);
        var validCreated1 = new MyEntity(1, FIRST_NAME);
        var toCreate2 = new MyEntity(null, SECOND_NAME);
        var validCreated2 = new MyEntity(2, SECOND_NAME);

        var validList = List.of(validCreated1, validCreated2);

        when(repository.findDuplication(any()))
            .thenReturn(Optional.empty());

        when(save.save(toCreate1))
            .thenReturn(validCreated1);
        when(save.save(toCreate2))
            .thenReturn(validCreated2);

        // When
        var createdList = NoDuplicationService.prepEntities(
            List.of(toCreate1, toCreate2),
            repository,
            save
        );

        // Then
        assertNotNull(createdList, "Created list should not be null");
        for (int i = 0; i < validList.size(); i++) {
            var created = createdList.get(i);
            var valid = validList.get(i);

            assertEquals(
                created,
                valid,
                String.format(
                    "Created (id: %d, name: %s) should contains the same values as Valid (id: %d, name: %s)",
                    created.getId(),
                    created.getName(),
                    valid.getId(),
                    valid.getName()
                )
            );
        }

        verify(repository, times(2)).findDuplication(any());
        verify(save).save(toCreate1);
        verify(save).save(toCreate2);
    }

    @Test
    void prepEntities_returns_null_for_empty_elements_in_provided_list() {
        // Given
        List<MyEntity> nullList = new ArrayList<>();

        for (int i = 0; i < 5; i++)
            nullList.add(null);

        // When
        var shouldBeNull = NoDuplicationService.prepEntities(
            nullList,
            repository,
            save
        );

        // Then
        assertNull(shouldBeNull, "Returned list should be null");

        verify(repository, never()).findDuplication(any());
        verify(save, never()).save(any());
    }

    @Test
    void prepEntitiesWithWriteModel_handles_null_vales() {
        //Given
        final var FIRST_NAME = "foo1";
        var toSave = new MyEntityWrite(FIRST_NAME);
        List<MyEntityWrite> listToSave = new ArrayList<>();

        listToSave.add(toSave);
        listToSave.add(null);

        when(repository.findDuplication(any()))
            .thenReturn(Optional.empty());
        when(save.save(toSave.toEntity()))
            .thenReturn(new MyEntity(1, FIRST_NAME));

        // When
        var savedWithNull = NoDuplicationService.prepEntitiesWithWriteModel(
            listToSave,
            repository,
            save
        );

        // Then
        assertEquals(
            1,
            savedWithNull.size(),
            "Returned list should contain only one created element."
        );

        verify(repository, times(1)).findDuplication(any());
        verify(save, times(1)).save(any());
    }

    @Test
    void prepEntitiesWithWriteModel_returns_null_for_empty_elements_in_provided_list() {
        // Given
        List<MyEntityWrite> nullList = new ArrayList<>();

        for (int i = 0; i < 5; i++)
            nullList.add(null);

        // When
        var shouldBeNull = NoDuplicationService.prepEntitiesWithWriteModel(
            nullList,
            repository,
            save
        );

        // Then
        assertNull(shouldBeNull, "Returned list should be null");

        verify(repository, never()).findDuplication(any());
        verify(save, never()).save(any());
    }
}