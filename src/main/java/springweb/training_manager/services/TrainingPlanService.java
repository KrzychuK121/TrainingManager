package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import springweb.training_manager.models.composite_ids.TrainingPlanId;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.models.viewmodels.training.WorkoutTrainingRead;
import springweb.training_manager.models.viewmodels.training_plan.TrainingPlanWrite;
import springweb.training_manager.models.viewmodels.training_plan.TrainingPlansEditRead;
import springweb.training_manager.models.viewmodels.training_plan.TrainingPlansWrite;
import springweb.training_manager.models.viewmodels.training_plan.TrainingReminderRead;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineReadIndex;
import springweb.training_manager.models.viewmodels.training_schedule.TrainingScheduleRead;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.repositories.for_controllers.TrainingPlanRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;
import springweb.training_manager.repositories.for_controllers.TrainingScheduleRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TrainingPlanService {
    private final TrainingPlanRepository repository;
    private final TrainingScheduleRepository scheduleRepository;
    private final TrainingRoutineService routineService;
    private final TrainingRepository trainingRepository;

    private Map<Weekdays, TrainingPlan> plansToWeekdayMap(List<TrainingPlan> plans) {
        return plans.stream()
            .collect(
                Collectors.toMap(
                    plan -> plan.getTrainingSchedule()
                        .getWeekday(),
                    plan -> plan
                )
            );
    }

    public Map<Integer, TrainingRoutineReadIndex> getMapFromPlans(List<TrainingPlan> plans) {
        Map<Integer, TrainingRoutineReadIndex> toReturn = new HashMap<>();
        plans.forEach(
            trainingPlan -> {
                var routine = trainingPlan.getTrainingRoutine();
                var routineId = routine.getId();
                if (!toReturn.containsKey(routineId))
                    toReturn.put(
                        routineId,
                        new TrainingRoutineReadIndex(
                            routineId,
                            routine.isActive()
                        )
                    );

                var weekday = trainingPlan.getTrainingSchedule()
                    .getWeekday();
                toReturn.get(routineId)
                    .putSchedule(
                        weekday,
                        new TrainingScheduleRead(
                            trainingPlan.getTrainingSchedule()
                        )
                    );

            }
        );

        return toReturn;
    }

    public List<TrainingRoutineReadIndex> getAllByUser(User owner) {
        List<TrainingPlan> all = repository.findByTrainingRoutineOwner(owner)
            .orElseThrow(() -> new IllegalArgumentException("Podany użytkownik nie istnieje lub nie posiada planów treningowych."));

        return new ArrayList<>(
            getMapFromPlans(all).values()
        );
    }

    public TrainingPlansEditRead getUserEditPlans(
        String ownerId,
        int routineId
    ) {
        List<TrainingPlan> plans = repository.findByTrainingRoutineOwnerIdAndTrainingRoutineId(
                ownerId,
                routineId
            )
            .orElseThrow(
                () -> new IllegalArgumentException(
                    "Podany użytkownik nie istnieje lub nie posiada planu treningowego o podanej nazwie."
                )
            );

        return new TrainingPlansEditRead(plans);
    }

    private List<TrainingPlan> getPlansByRoutineId(int trainingRoutineId) {
        return repository.findByTrainingRoutineId(trainingRoutineId)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie istnieje plan przypisany do podanej rutyny")
            );
    }

    public List<TrainingPlan> getUserActivePlans(String userId) {
        return getPlansByRoutineId(
            routineService.getUserActiveRoutine(userId)
                .getId()
        );
    }

    public Map<Weekdays, TrainingPlan> getUserActivePlansMap(String userId) {
        return plansToWeekdayMap(
            getUserActivePlans(userId)
        );
    }

    private List<TrainingPlan> getTodayPlans(String userId) {
        var plans = getUserActivePlans(userId);
        var today = LocalDateTime.now()
            .getDayOfWeek();
        var todayPlans = plans.stream()
            .filter(
                plan -> (
                    plan.getTrainingSchedule()
                        .getWeekday()
                        .toString()
                        .equals(today.toString())
                )
            )
            .toList();

        if (todayPlans.isEmpty())
            return null;

        return todayPlans;
    }

    public TrainingReminderRead getUserTrainingReminder(
        String userId,
        String reminderTitle
    ) {
        return getUserTrainingReminder(
            userId,
            reminderTitle,
            ReminderType.INIT
        );
    }

    public TrainingReminderRead getUserTrainingReminder(
        String userId,
        String reminderTitle,
        ReminderType type
    ) {
        var todayPlans = getTodayPlans(userId);
        if (todayPlans == null)
            return null;

        TrainingPlan todayPlan = todayPlans.get(0);
        Training todayTraining = todayPlan
            .getTrainingSchedule()
            .getTraining();

        return new TrainingReminderRead(
            reminderTitle,
            todayTraining.getTitle(),
            todayPlan.getTrainingTime(),
            type
        );
    }

    public WorkoutTrainingRead getUserActiveTraining(String userId) {
        var todayPlans = getTodayPlans(userId);
        if (todayPlans == null)
            return null;

        Training todayTraining = todayPlans.get(0)
            .getTrainingSchedule()
            .getTraining();

        return new WorkoutTrainingRead(
            todayPlans.get(0)
                .getTrainingRoutineId(),
            new TrainingRead(todayTraining)
        );
    }

    public TrainingSchedule prepTrainingSchedule(TrainingSchedule schedule) {
        TrainingSchedule prepared = NoDuplicationService.prepEntity(
            schedule,
            scheduleRepository,
            scheduleRepository::save
        );

        if (prepared.getTraining() == null)
            prepared.setTraining(
                trainingRepository.findById(prepared.getTrainingId())
                    .orElseThrow(
                        () -> new IllegalArgumentException("There is no training with provided id.")
                    )
            );

        return prepared;
    }

    public Map<String, List<String>> validateTrainingPlans(
        TrainingPlansWrite data,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(
                result,
                "planWriteMap."
            );
            return validation.getErrors();
        }

        return null;
    }

    public boolean planInvalid(TrainingPlanWrite planWrite) {
        if (planWrite == null)
            return true;

        var trainingId = planWrite.getTrainingId();
        return trainingId == 0 ||
            !trainingRepository.existsById(trainingId);
    }

    public List<TrainingPlan> createNewPlans(List<TrainingPlan> plans, User owner) {
        List<TrainingPlan> created = new ArrayList<>(plans.size());
        plans.forEach(
            plan -> {
                TrainingSchedule prepared = prepTrainingSchedule(plan.getTrainingSchedule());
                int scheduleId = prepared.getId();

                TrainingPlanId planId = new TrainingPlanId(
                    plan.getTrainingRoutine()
                        .getId(),
                    scheduleId
                );

                plan.setId(planId);
                plan.setTrainingSchedule(prepared);

                created.add(repository.save(plan));
            }
        );

        return created;
    }

    public List<TrainingPlan> createNewPlans(TrainingPlansWrite plansWrite, User owner) {
        var weekdays = Weekdays.values();
        List<TrainingPlan> plans = new ArrayList<>(weekdays.length);
        TrainingRoutine routine = routineService.createNewByUser(
            owner
        );

        for (var weekday : weekdays) {
            TrainingPlanWrite planWrite = plansWrite.getPlanWriteMap()
                .get(weekday);
            if (planInvalid(planWrite))
                continue;

            var timeToSave = TimeService.parseTime(planWrite.getTrainingTime());
            var toAdd = new TrainingPlan(
                routine,
                new TrainingSchedule(
                    planWrite.getTrainingId(),
                    weekday
                ),
                timeToSave
            );

            plans.add(toAdd);
        }

        if (plans.isEmpty()) {
            routineService.delete(routine);
            throw new IllegalStateException("Nie utworzono planu ponieważ nie przypisano żadnego treningu.");
        }

        return createNewPlans(plans, owner);
    }

    public List<TrainingPlan> edit(
        TrainingPlansWrite plansWrite,
        int routineId,
        User user
    ) {
        var routine = routineService.getById(routineId, user.getId());
        Map<Weekdays, TrainingPlan> originalPlans = plansToWeekdayMap(
            getPlansByRoutineId(routineId)
        );

        var weekdays = Weekdays.values();
        List<TrainingPlan> plans = new ArrayList<>(weekdays.length);

        for (var weekday : weekdays) {
            TrainingPlanWrite planWrite = plansWrite.getPlanWriteMap()
                .get(weekday);

            var originalPlan = originalPlans.getOrDefault(weekday, null);

            var edited = edit(
                planWrite,
                routine,
                weekday,
                originalPlan
            );
            if (edited != null)
                plans.add(
                    edited
                );
        }

        return plans;
    }

    public TrainingPlan edit(
        TrainingPlanWrite toEdit,
        TrainingRoutine routine,
        Weekdays weekday,
        TrainingPlan original
    ) {
        if (original != null) {
            repository.delete(original);

            var trainingSchedulesCount = repository.countByTrainingScheduleId(
                original.getTrainingScheduleId()
            );
            if (trainingSchedulesCount == 0)
                scheduleRepository.deleteById(
                    original.getTrainingScheduleId()
                );
        }

        if (toEdit == null)
            return null;

        var timeToSave = TimeService.parseTime(toEdit.getTrainingTime());
        var prepared = prepTrainingSchedule(
            new TrainingSchedule(
                toEdit.getTrainingId(),
                weekday
            )
        );

        TrainingPlan edited = new TrainingPlan(
            routine,
            prepared,
            timeToSave
        );
        return repository.save(edited);
    }


}
