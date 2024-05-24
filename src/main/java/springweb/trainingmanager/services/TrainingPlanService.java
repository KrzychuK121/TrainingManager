package springweb.trainingmanager.services;

import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.*;
import springweb.trainingmanager.models.viewmodels.trainingPlan.TrainingPlanWrite;
import springweb.trainingmanager.models.viewmodels.trainingPlan.TrainingPlansWrite;
import springweb.trainingmanager.models.viewmodels.trainingRoutine.TrainingRoutineReadIndex;
import springweb.trainingmanager.models.viewmodels.trainingSchedule.TrainingScheduleRead;
import springweb.trainingmanager.repositories.forcontrollers.TrainingPlanRepository;
import springweb.trainingmanager.repositories.forcontrollers.TrainingScheduleRepository;

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


    private Map<Integer, TrainingRoutineReadIndex> getMapFromPlans(List<TrainingPlan> plans){
        Map<Integer, TrainingRoutineReadIndex> toReturn = new HashMap<>();
        plans.forEach(
            trainingPlan -> {
                var routine = trainingPlan.getTrainingRoutine();
                var routineId = routine.getId();
                if(!toReturn.containsKey(routineId))
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

    public List<TrainingRoutineReadIndex> getAllByUser(User owner){
        List<TrainingPlan> all = repository.findByTrainingRoutineOwner(owner)
            .orElseThrow(() -> new IllegalArgumentException("Podany użytkownik nie istnieje lub nie posiada planów treningowych."));

        return new ArrayList<>(
            getMapFromPlans(all).values()
        );
    }

    private List<TrainingPlan> getPlansByRoutineId(int trainingRoutineId){
        return repository.findByTrainingRoutineId(trainingRoutineId)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie istnieje plan przypisany do podanej rutyny")
            );
    }

    public Map<Weekdays, TrainingPlan> getUserActivePlans(String userId){
        return getPlansByRoutineId(
            routineService.getUserActiveRoutine(userId)
            .getId()
        ).stream().collect(
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

    public List<TrainingPlan> createNewPlans(List<TrainingPlan> plans){
        // TODO: Finish this method and test it
        List<TrainingPlan> created = new ArrayList<>(plans.size());
        plans.forEach(
            plan ->{
                plan.setTrainingSchedule(prepTrainingSchedule(plan));
                created.add(repository.save(plan));
            }
        );

        return created;
    }

    public List<TrainingPlan> createNewPlans(TrainingPlansWrite plansWrite, User owner){
        var weekdays = Weekdays.values();
        List<TrainingPlan> plans = new ArrayList<>(weekdays.length);
        TrainingRoutine routine = routineService.createNewByUser(
            owner
        );

        for(var weekday : weekdays) {
            TrainingPlanWrite planWrite = plansWrite.getPlanWriteMap().get(weekday);
            var trainingId = planWrite.getTrainingId();

            if(trainingId == 0 || !trainingService.existsById(trainingId))
                continue;

            LocalTime trainingTime = null;
            if(planWrite.getTrainingTime() != null && !planWrite.getTrainingTime().isEmpty()){
                String[] time = planWrite.getTrainingTime().split(":");
                try {
                    trainingTime = LocalTime.of(
                        Integer.parseInt(time[0]),
                        Integer.parseInt(time[1]),
                        0
                    );
                } catch(DateTimeException ignored) { }
            }


            var toAdd = new TrainingPlan(
                routine,
                new TrainingSchedule(trainingId, weekday),
                trainingTime
            );

            plans.add(toAdd);
        }

        if(plans.isEmpty()){
            routineService.delete(routine);
            throw new IllegalStateException("Nie utworzono planu ponieważ nie przypisano żadnego treningu.");
        }

        return createNewPlans(plans);
    }


}
