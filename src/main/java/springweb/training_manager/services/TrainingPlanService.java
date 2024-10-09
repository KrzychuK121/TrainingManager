package springweb.training_manager.services;

import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.models.schemas.TrainingPlanId;
import springweb.training_manager.models.viewmodels.training_plan.TrainingPlanWrite;
import springweb.training_manager.models.viewmodels.training_plan.TrainingPlansWrite;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineReadIndex;
import springweb.training_manager.models.viewmodels.training_schedule.TrainingScheduleRead;
import springweb.training_manager.repositories.for_controllers.TrainingPlanRepository;
import springweb.training_manager.repositories.for_controllers.TrainingScheduleRepository;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {
    private final TrainingPlanRepository repository;
    private final TrainingScheduleRepository scheduleRepository;
    private final TrainingRoutineService routineService;
    private final TrainingService trainingService;

    public TrainingPlanService(
        final TrainingPlanRepository repository,
        final TrainingScheduleRepository scheduleRepository,
        final TrainingRoutineService routineService,
        final TrainingService trainingService
    ) {
        this.repository = repository;
        this.scheduleRepository = scheduleRepository;
        this.routineService = routineService;
        this.trainingService = trainingService;
    }


    private Map<Integer, TrainingRoutineReadIndex> getMapFromPlans(List<TrainingPlan> plans) {
        Map<Integer, TrainingRoutineReadIndex> toReturn = new HashMap<>();
        plans.forEach(
            trainingPlan -> {
                var routine = trainingPlan.getTrainingRoutine();
                var routineId = routine.getId();
                if (!toReturn.containsKey(routineId))
                    toReturn.put(
                        routineId,
                        new TrainingRoutineReadIndex(
                            routine.isActive()
                        )
                    );

                var weekday = trainingPlan.getTrainingSchedule().getWeekday();
                toReturn.get(routineId).putSchedule(
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
        return getUserActivePlans(userId).stream().collect(
            Collectors.toMap(
                o -> o.getTrainingSchedule().getWeekday(),
                o -> o
            )
        );
    }

    private TrainingSchedule prepTrainingSchedule(TrainingPlan plan) {
        return NoDuplicationService.prepEntity(
            plan.getTrainingSchedule(),
            scheduleRepository,
            scheduleRepository::save
        );
    }

    public List<TrainingPlan> createNewPlans(List<TrainingPlan> plans) {
        List<TrainingPlan> created = new ArrayList<>(plans.size());
        plans.forEach(
            plan -> {
                TrainingSchedule prepared = prepTrainingSchedule(plan);
                int scheduleId = prepared.getId();

                TrainingPlanId planId = new TrainingPlanId(
                    plan.getTrainingRoutine().getId(),
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
            TrainingPlanWrite planWrite = plansWrite.getPlanWriteMap().get(weekday);
            var trainingId = planWrite.getTrainingId();

            if (trainingId == 0 || !trainingService.existsById(trainingId))
                continue;

            LocalTime trainingTime = null;
            if (planWrite.getTrainingTime() != null && !planWrite.getTrainingTime().isEmpty()) {
                String[] time = planWrite.getTrainingTime().split(":");
                try {
                    trainingTime = LocalTime.of(
                        Integer.parseInt(time[0]),
                        Integer.parseInt(time[1]),
                        0
                    );
                } catch (DateTimeException ignored) {
                }
            }


            var toAdd = new TrainingPlan(
                routine,
                new TrainingSchedule(trainingId, weekday),
                trainingTime
            );

            plans.add(toAdd);
        }

        if (plans.isEmpty()) {
            routineService.delete(routine);
            throw new IllegalStateException("Nie utworzono planu ponieważ nie przypisano żadnego treningu.");
        }

        return createNewPlans(plans);
    }


}
