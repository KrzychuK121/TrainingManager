package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import springweb.training_manager.models.schemas.ExerciseSchema;

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
    @Valid
    private ExerciseParameters parameters;
    @OneToMany(mappedBy = "exercise")
    private List<TrainingExercise> trainingExercises = new ArrayList<>();

    public Exercise(
        String name,
        String description,
        BodyPart bodyPart,
        ExerciseParameters parameters,
        int defaultBurnedKcal
    ) {
        super(
            0,
            name,
            description,
            bodyPart,
            defaultBurnedKcal
        );
        this.parameters = parameters;
    }

    public List<Training> getTrainings() {
        return trainingExercises.stream()
            .map(TrainingExercise::getTraining)
            .toList();
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
            toEdit.parameters.getTime()
        );
        bodyPart = toEdit.bodyPart;
        defaultBurnedKcal = toEdit.defaultBurnedKcal;
        trainingExercises = toEdit.trainingExercises == null || toEdit.trainingExercises.isEmpty() ?
            null :
            toEdit.trainingExercises;
    }
}
