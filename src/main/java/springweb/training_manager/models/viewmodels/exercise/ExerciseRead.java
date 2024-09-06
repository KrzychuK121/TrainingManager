package springweb.training_manager.models.viewmodels.exercise;

import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.viewmodels.training.TrainingExercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseRead extends ExerciseSchema {
    private List<TrainingExercise> trainings = new ArrayList<>();

    public ExerciseRead(Exercise exercise) {
        this.id = exercise.getId();
        this.name = exercise.getName();
        this.description = exercise.getDescription();
        this.rounds = exercise.getRounds();
        this.repetition = exercise.getRepetition();
        this.weights = exercise.getWeights();
        this.time = exercise.getTime();
        this.bodyPart = exercise.getBodyPart();
        this.difficulty = exercise.getDifficulty();
        this.trainings = TrainingExercise.toTrainingExerciseList(exercise.getTrainings());
    }

    public static List<ExerciseRead> toExerciseReadList(final List<Exercise> list){
        List<ExerciseRead> result = new ArrayList<>(list.size());
        list.forEach(exercise -> result.add(new ExerciseRead(exercise)));
        return result;
    }

    public List<TrainingExercise> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TrainingExercise> trainings) {
        this.trainings = trainings;
    }
}