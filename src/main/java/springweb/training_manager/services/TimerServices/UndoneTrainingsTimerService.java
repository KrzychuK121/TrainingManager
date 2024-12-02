package springweb.training_manager.services.TimerServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.entities.Weekdays;
import springweb.training_manager.models.viewmodels.done_training.DoneTrainingWrite;
import springweb.training_manager.repositories.for_controllers.TrainingPlanRepository;
import springweb.training_manager.services.DoneTrainingService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class UndoneTrainingsTimerService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final TrainingPlanRepository trainingPlanRepository;
    private final DoneTrainingService doneTrainingService;
    private final long INCOMING_TRAINING_TRACKING_FIXED_RATE = 1;

    private void handleSavingStatisticsForPlan(TrainingPlan plan) {
        var startDate = LocalDate.now();
        var startDateTime = startDate.atTime(plan.getTrainingTime());
        var user = plan.getTrainingRoutine()
            .getOwner();
        var doneTraining = new DoneTrainingWrite(
            plan.getTrainingRoutineId(),
            plan.getTrainingSchedule()
                .getTrainingId(),
            startDateTime,
            null,
            null
        );

        doneTrainingService.createIfNotExist(
            doneTraining,
            user
        );
    }

    private void scheduleByPlan(
        TrainingPlan plan,
        LocalTime nowTime
    ) {
        var planTime = plan.getTrainingTime();
        if (nowTime.isAfter(planTime)) {
            handleSavingStatisticsForPlan(plan);
            return;
        }
        var delay = Duration.between(nowTime, planTime)
            .getSeconds();
        scheduler.schedule(
            () -> handleSavingStatisticsForPlan(plan),
            delay,
            TimeUnit.SECONDS
        );
    }

    @Scheduled(
        fixedRate = INCOMING_TRAINING_TRACKING_FIXED_RATE,
        timeUnit = TimeUnit.HOURS
    )
    public void trackTodayTrainingsToRegisterUndone() {
        var nowDate = LocalDateTime.now();
        var nowTime = nowDate.toLocalTime();
        var today = Weekdays.getByDate(nowDate);

        log.info("Retrieving incoming trainings...");

        List<TrainingPlan> incomingTrainings = trainingPlanRepository.findAllActivePlansForWeekdayAndTrainingTimeBetween(
            today,
            nowTime,
            nowTime
                .plusHours(INCOMING_TRAINING_TRACKING_FIXED_RATE)
        );
        if (incomingTrainings.isEmpty()) {
            var pattern = DateTimeFormatter.ofPattern("HH:mm");
            log.info(
                "There is no incoming trainings between {} and {}",
                nowTime.format(pattern),
                nowTime.plusHours(INCOMING_TRAINING_TRACKING_FIXED_RATE)
                    .format(pattern)
            );
            return;
        }
        log.info("Incoming trainings retrieved.");
        incomingTrainings.forEach(
            plan -> scheduleByPlan(
                plan,
                nowDate.toLocalTime()
            )
        );
    }
}
