package springweb.trainingmanager.services;

import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.TrainingPlan;
import springweb.trainingmanager.models.entities.Weekdays;
import springweb.trainingmanager.repositories.forcontrollers.TrainingPlanRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {
    private final TrainingPlanRepository repository;
    private final TrainingRoutineService routineService;

    public TrainingPlanService(
        final TrainingPlanRepository repository,
        final TrainingRoutineService routineService
    ) {
        this.repository = repository;
        this.routineService = routineService;
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

    private List<TrainingPlan> getPlansByRoutineId(int trainingRoutineId){
        return repository.findByTrainingRoutineId(trainingRoutineId)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie istnieje plan przypisany do podanej rutyny")
            );
    }
}
