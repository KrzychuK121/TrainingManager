package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.TrainingSchema;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "training")
public class Training extends TrainingSchema {
    @OneToMany(mappedBy = "training")
    private List<TrainingExercise> trainingExercises = new ArrayList<>();

    @ManyToOne
    @JoinColumn(
        name = "owner_id",
        referencedColumnName = "id"
    )
    private User owner;

    public Training(
        String title,
        String description
    ) {
        super(
            0,
            title,
            description
        );
    }

    public List<Exercise> getExercises() {
        return trainingExercises.stream()
            .map(TrainingExercise::getExercise)
            .toList();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Might be used in PUT but NOT PATCH!!
    public void copy(Training toCopy) {
        this.title = toCopy.title;
        this.description = toCopy.description;
        if (toCopy.trainingExercises != null)
            this.trainingExercises = toCopy.trainingExercises;
        this.owner = toCopy.owner;
    }
}
