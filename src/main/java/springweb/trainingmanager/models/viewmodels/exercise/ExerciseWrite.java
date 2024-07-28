package springweb.trainingmanager.models.viewmodels.exercise;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import springweb.trainingmanager.models.entities.BodyPart;
import springweb.trainingmanager.models.entities.Difficulty;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.schemas.ExerciseSchema;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExerciseWrite extends ExerciseSchema {
    private List<TrainingExercise> trainings = new ArrayList<>();

    public ExerciseWrite() {
    }

    public static List<Exercise> toExerciseList(final List<ExerciseWrite> list){
        List<Exercise> result = new ArrayList<>(list.size());
        list.forEach(exerciseWrite -> {
            var toSave = new Exercise();

            toSave.setName(exerciseWrite.name);
            toSave.setDescription(exerciseWrite.description);
            toSave.setRounds(exerciseWrite.rounds);
            toSave.setRepetition(exerciseWrite.repetition);
            toSave.setWeights(exerciseWrite.weights);
            toSave.setTime(exerciseWrite.time);
            toSave.setBodyPart(exerciseWrite.bodyPart);
            toSave.setTrainings(
                TrainingExercise.toTrainingList(
                    exerciseWrite.trainings
                )
            );

            result.add(toSave);
        });

        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public void setWeights(short weights){
        this.weights = weights;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setBodyPart(BodyPart bodyPart){
        this.bodyPart = bodyPart;
    }

    public void  setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<TrainingExercise> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TrainingExercise> trainings) {
        this.trainings = trainings;
    }

    public Exercise toExercise(){
        var toReturn = new Exercise();

        toReturn.setName(name);
        toReturn.setDescription(description);
        toReturn.setRounds(rounds);
        toReturn.setRepetition(repetition);
        toReturn.setWeights(weights);
        toReturn.setTime(time);
        toReturn.setBodyPart(bodyPart);
        toReturn.setDifficulty(difficulty);

        if(trainings != null)
            toReturn.setTrainings(TrainingExercise.toTrainingList(trainings));

        return toReturn;
    }
}
