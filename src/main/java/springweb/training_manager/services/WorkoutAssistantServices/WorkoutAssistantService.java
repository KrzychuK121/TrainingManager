package springweb.training_manager.services.WorkoutAssistantServices;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.models.viewmodels.workout_assistant.PlannedRoutineRead;
import springweb.training_manager.models.viewmodels.workout_assistant.WorkoutAssistantWrite;
import springweb.training_manager.repositories.for_controllers.*;
import springweb.training_manager.services.NoDuplicationService;
import springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantTypedServices.WATypedBase;

import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutAssistantService {
    private final TrainingRepository trainingRepository;
    private final TrainingExerciseRepository trainingExerciseRepository;
    private final TrainingRoutineRepository trainingRoutineRepository;
    private final TrainingScheduleRepository trainingScheduleRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final ExerciseParametersRepository parametersRepository;

    private static final Map<User, TrainingRoutine> plannedUsersRoutines = Collections.synchronizedMap(new HashMap<>());

    private Training prepareTrainingToSave(
        Training training,
        TrainingRoutine routine
    ) {
        if (
            !training.getId()
                .equals(training.defaultId())
        )
            return training;

        var otherTraining = new Training();
        otherTraining.copy(training);
        otherTraining.setTrainingExercises(List.of());
        otherTraining.setTitle(
            String.format(
                "%s#%d",
                otherTraining.getTitle(),
                routine.getId()
            )
        );

        var found = trainingRepository.findDuplication(otherTraining);
        return found.orElseGet(
            () -> trainingRepository.save(otherTraining)
        );
    }

    @Transactional
    public void savePlannedRoutine(User user) {
        var routine = plannedUsersRoutines.get(user);
        if (routine == null)
            throw new IllegalStateException(
                "User " + user.getUsername() + " does not have planned routine yet."
            );

        var newRoutine = new TrainingRoutine();
        newRoutine.setOwner(user);

        var savedRoutine = trainingRoutineRepository.save(newRoutine);
        var plans = routine.getPlans();
        plans.forEach(
            plan -> {
                var schedule = plan.getTrainingSchedule();
                var training = schedule.getTraining();
                var preparedTraining = prepareTrainingToSave(
                    training,
                    newRoutine
                );

                schedule.setTraining(preparedTraining);
                if (
                    preparedTraining.getTrainingExercises()
                        .isEmpty()
                ) {
                    List<TrainingExercise> preparedTrainingExercises = training.getTrainingExercises()
                        .stream()
                        .map(
                            trainingExercise -> {
                                var preparedParameters = NoDuplicationService.prepEntity(
                                    trainingExercise.getParameters(),
                                    parametersRepository,
                                    parametersRepository::save
                                );
                                trainingExercise.setParameters(preparedParameters);
                                trainingExercise.setTraining(preparedTraining);

                                return trainingExerciseRepository.save(
                                    new TrainingExercise(
                                        preparedTraining,
                                        trainingExercise.getExercise(),
                                        preparedParameters
                                    )
                                );
                            }
                        )
                        .toList();

                    preparedTraining.setTrainingExercises(preparedTrainingExercises);
                }

                var preparedSchedule = NoDuplicationService.prepEntity(
                    new TrainingSchedule(
                        preparedTraining,
                        schedule.getWeekday()
                    ),
                    trainingScheduleRepository,
                    trainingScheduleRepository::save
                );

                var newTrainingPlan = new TrainingPlan(
                    savedRoutine,
                    preparedSchedule,
                    plan.getTrainingTime()
                );
                trainingPlanRepository.save(newTrainingPlan);
            }
        );

        plannedUsersRoutines.remove(user);
    }

    public ValidationErrors validateAndPrepare(
        WorkoutAssistantWrite workoutAssistantWrite,
        BindingResult result
    ) {
        if (result.hasErrors())
            return ValidationErrors.createFrom(result);

        var earliest = workoutAssistantWrite.getEarliestTrainingStart();
        var latest = workoutAssistantWrite.getLatestTrainingStart();

        if (earliest.isAfter(latest)) {
            workoutAssistantWrite.setEarliestTrainingStart(latest);
            workoutAssistantWrite.setLatestTrainingStart(earliest);
        }

        return null;
    }

    /**
     * This method returns next day of the week based on index. The idea is to generate
     * days with as many gap days for rest as possible. E.g. if loop will be executed 4
     * times, the function will return values: SUNDAY, FRIDAY, WEDNESDAY, MONDAY.
     * <p>
     * If loop iterates more than 4 times, function will generate days in the same way but
     * starting from SATURDAY
     *
     * @param index value from 0 to 7 (one day for training or whole week).
     *
     * @return Weekdays value that has 1 day gap from previous day.
     */
    private static Weekdays getNextWeekday(int index) {
        var weekdays = Weekdays.values();
        var weekdaysLastIndex = weekdays.length - 1;
        var decrease = index < 4
            ? 0
            : 7;
        var nextWeekdaysIndex = weekdaysLastIndex - (2 * index - decrease);
        return weekdays[nextWeekdaysIndex];
    }

    private static LocalTime getTimeInRange(WorkoutAssistantWrite workoutAssistantWrite) {
        var earliestTime = workoutAssistantWrite.getEarliestTrainingStart();
        var latestTime = workoutAssistantWrite.getLatestTrainingStart();

        var earliestSec = earliestTime.toSecondOfDay();
        var latestSec = latestTime.toSecondOfDay();
        var middleTimeInSeconds = (earliestSec + latestSec) / 2;

        return LocalTime.ofSecondOfDay(middleTimeInSeconds);
    }

    private List<TrainingSchedule> planSchedules(
        WorkoutAssistantWrite workoutAssistantWrite,
        Map<BodyPart, List<Training>> bodyPartsTrainings
    ) {
        var schedules = new ArrayList<TrainingSchedule>(
            workoutAssistantWrite.getWorkoutDays()
        );

        var workoutDays = workoutAssistantWrite.getWorkoutDays();
        for (int i = 0; i < workoutDays; i++) {
            var nextBodyPart = bodyPartsTrainings.keySet()
                .stream()
                .toList()
                .get(i % bodyPartsTrainings.size());
            var trainings = bodyPartsTrainings.get(nextBodyPart);

            var nextTrainingIndex = i % trainings.size();
            var nextTraining = trainings.get(nextTrainingIndex);
            var nextDay = getNextWeekday(i);

            var newSchedule = new TrainingSchedule(
                nextTraining,
                nextDay
            );

            schedules.add(
                trainingScheduleRepository.findDuplication(newSchedule)
                    .orElse(newSchedule)
            );
        }

        return schedules;
    }

    public PlannedRoutineRead planTrainingRoutine(
        WorkoutAssistantWrite workoutAssistantWrite,
        WATypedBase waTypedBase,
        User loggedUser
    ) {
        Map<BodyPart, List<Training>> trainings = waTypedBase.getTrainingsByBodyParts(
            workoutAssistantWrite.getBodyParts(),
            loggedUser
        );

        trainings = waTypedBase.prepTrainingsForUserGoal(
            workoutAssistantWrite,
            trainings
        );

        var schedules = planSchedules(
            workoutAssistantWrite,
            trainings
        );

        var newRoutine = new TrainingRoutine();
        newRoutine.setOwner(loggedUser);

        var plans = schedules.stream()
            .map(
                schedule -> new TrainingPlan(
                    newRoutine,
                    schedule,
                    getTimeInRange(workoutAssistantWrite)
                )
            )
            .toList();
        newRoutine.setPlans(plans);

        plannedUsersRoutines.put(loggedUser, newRoutine);
        return new PlannedRoutineRead(plans);
    }
}