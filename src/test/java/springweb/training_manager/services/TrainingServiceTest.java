package springweb.training_manager.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.models.view_models.exercise.ExerciseTraining;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrainingServiceTest {

    private ExerciseRepository repositoryFindByExercise(
        List<Exercise> toFind,
        List<Boolean> found
    ) {
        var mock = mock(ExerciseRepository.class);

        for (int i = 0; i < toFind.size(); i++) {
            when(mock.findDuplication(toFind.get(i)))
                .thenReturn(
                    Optional.of(
                        copy(toFind.get(i), found.get(i) ? 9 : 0)
                    )
                );

            if (!found.get(i))
                when(mock.save(toFind.get(i)))
                    .thenReturn(
                        copy(toFind.get(i), 9)
                    );
        }


        return mock;
    }

    private Exercise copy(Exercise toCopy, int id) {
        var copyOf = new Exercise();

        var parameters = toCopy.getParameters();

        copyOf.setId(id);
        copyOf.copy(toCopy);

        return copyOf;
    }

    @Test
    @DisplayName("prepExercises should create two new entity and return them")
    void prepExercisesShouldCreateTwoNewEntity() {
        // given
        var user = new User(
            "admin",
            "admin",
            "administrator",
            "qwerty123"
        );

        user.setRole(Role.ADMIN);

        var first = new Exercise(
            "pompki",
            "zwykłe pompki",
            BodyPart.CHEST,
            new ExerciseParameters(
                9,
                3,
                20,
                (short) 0,
                LocalTime.of(0, 0, 0)
            ),
            1
        );

        var second = new Exercise(
            "brzuszki",
            "brzuszki z obciążeniem 4,5kg na rękę",
            BodyPart.UPPER_ABS,
            new ExerciseParameters(
                10,
                3,
                20,
                (short) 4,
                LocalTime.of(0, 0, 0)
            ),
            2
        );

        var newFirst = copy(first, 9);
        var newSecond = copy(second, 9);

        var exerciseList = List.of(first, second);

        // when
        var service = new TrainingService(
            repositoryFindByExercise(
                exerciseList,
                List.of(false, false)
            ),
            null,
            null,
            null,
            null
        );
        var result = service.prepExercises(
            ExerciseTraining.toExerciseTrainingList(
                exerciseList
            ),
            user
        );

        // then
        result.forEach(
            exercise -> System.out.println(
                exercise.getId() + "\t" +
                    exercise.getName() + "\t" +
                    exercise.getDescription()
            )
        );
        assertThat(result).contains(newFirst, newSecond);

    }
}