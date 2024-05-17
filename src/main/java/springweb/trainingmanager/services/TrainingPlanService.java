package springweb.trainingmanager.services;

import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.TrainingPlan;
import springweb.trainingmanager.models.entities.Weekdays;
import springweb.trainingmanager.repositories.forcontrollers.TrainingPlanRepository;
import springweb.trainingmanager.repositories.forcontrollers.TrainingScheduleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {
    private final TrainingPlanRepository repository;
    private final TrainingScheduleRepository scheduleRepository;
    private final TrainingRoutineService routineService;

    public TrainingPlanService(
        final TrainingPlanRepository repository,
        final TrainingScheduleRepository scheduleRepository,
        final TrainingRoutineService routineService
    ) {
        this.repository = repository;
        this.scheduleRepository = scheduleRepository;
        this.routineService = routineService;
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

    public List<TrainingPlan> createNewPlans(List<TrainingPlan> plans){
        // TODO: Finish this method and test it
        List<TrainingPlan> created = new ArrayList<>(plans.size());
        plans.forEach(
            plan ->{
                plan.setTrainingSchedule(
                    NoDuplicationService.prepEntity(
                        plan.getTrainingSchedule(),
                        scheduleRepository,
                        scheduleRepository::save
                    )
                );

                created.add(repository.save(plan));
            }
        );

        return created;
    }


}
