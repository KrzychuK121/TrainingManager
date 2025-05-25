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
    protected boolean archived = false;

    @ManyToOne
    @JoinColumn(
        name = "owner_id",
        referencedColumnName = "id"
    )
    private User owner;

    public Training(
        String title,
        String description,
        boolean archived
    ) {
        super(
            0,
            title,
            description
        );
        this.archived = archived;
    }

    public Training(
        String title,
        String description
    ) {
        this(
            title,
            description,
            false
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
        var that = this;
        title = toCopy.title;
        description = toCopy.description;
        if (toCopy.trainingExercises != null)
            trainingExercises = toCopy.trainingExercises;
        trainingExercises.forEach(
            trainingExercise -> trainingExercise.setTraining(that)
        );
        owner = toCopy.owner;
    }
}
