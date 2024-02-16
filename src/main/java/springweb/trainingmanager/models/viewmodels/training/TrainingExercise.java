package springweb.trainingmanager.models.viewmodels.training;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import springweb.trainingmanager.models.entities.Training;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrainingExercise {
    private final int id;
    @NotBlank(message = "Tytuł treningu jest wymagany")
    @Length(min = 3, max = 100, message = "Tytuł musi mieścić się między 3 a 100 znaków")
    protected String title;
    @NotBlank(message = "Opis nie może być pusty")
    @Length(min = 3, max = 300, message = "Opis musi mieścić się między 3 a 300 znaków")
    protected String description;

    public TrainingExercise(){
        id = 0;
    }

    public TrainingExercise(Training training, int id) {
        this.id = id;
        this.title = training.getTitle();
        this.description = training.getDescription();
    }

    public static List<TrainingExercise> toTrainingExerciseList(final List<Training> list){
        List<TrainingExercise> result = new ArrayList<>(list.size());
        list.forEach(training -> result.add(new TrainingExercise(training, training.getId())));
        return result;
    }

    public static List<Training> toTrainingList(final List<TrainingExercise> list){
        List<Training> result = new ArrayList<>(list.size());
        list.forEach(trainingExercise -> {
            var toSave = new Training();

            toSave.setId(trainingExercise.getId());
            toSave.setId(trainingExercise.getId());
            toSave.setTitle(trainingExercise.title);
            toSave.setDescription(trainingExercise.description);

            result.add(toSave);
        });

        return result;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Training toTraining(){
        var toReturn = new Training();

        toReturn.setTitle(title);
        toReturn.setDescription(description);

        return toReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingExercise that = (TrainingExercise) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(description, that.description);
    }
}
