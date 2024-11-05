package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import springweb.training_manager.models.schemas.ExerciseSchema;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "exercise")
public class Exercise extends ExerciseSchema {
    @ManyToOne
    @JoinColumn(
        name = "parameters_id",
        referencedColumnName = "id"
    )
    private ExerciseParameters parameters;
    @ManyToMany(mappedBy = "exercises")
    @Valid
    private List<Training> trainings = new ArrayList<>();
    @OneToMany(mappedBy = "exercise")
    private List<TrainingExercise> trainingExercises = new ArrayList<>();

    public Exercise(
        String name,
        String description,
        BodyPart bodyPart,
        ExerciseParameters parameters
    ) {
        super(
            0,
            name,
            description,
            bodyPart
        );
        this.parameters = parameters;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    // Might be used in PUT but NOT PATCH!!
    public void copy(Exercise toEdit) {
        name = toEdit.name;
        description = toEdit.description;
        parameters = toEdit.parameters == null
            ? null
            : new ExerciseParameters(
                toEdit.parameters.getId(),
                toEdit.parameters.getRounds(),
                toEdit.parameters.getRepetition(),
                toEdit.parameters.getWeights(),
                toEdit.parameters.getTime(),
                toEdit.parameters.getDifficulty()
            );
        bodyPart = toEdit.bodyPart;
        trainings = toEdit.trainings == null || toEdit.trainings.isEmpty() ?
            null :
            toEdit.trainings;
    }
}
