package springweb.training_manager.services.TimerServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import springweb.training_manager.controllers.web_sockets.TrainingPlanControllerWS;
import springweb.training_manager.models.entities.ReminderType;
import springweb.training_manager.models.viewmodels.training_plan.TrainingReminderRead;
import springweb.training_manager.services.TrainingPlanService;
import springweb.training_manager.services.UserService;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationTimerService extends TimerService {
    private final TrainingPlanService trainingPlanService;
    private final SimpMessageSendingOperations messageTemplate;
    public static final String FIRST_REMINDER_TITLE = "Przypomnienie o treningu na dziś";
    public static final String BEFORE_TIMER_ID = "before";
    public static final String NOW_TIMER_ID = "now";

    private long calculateSecondsBeforeTime(
        int minutesBefore,
        LocalTime time
    ) {
        LocalTime timeBefore = time.minusMinutes(minutesBefore);
        Duration duration = Duration.between(LocalTime.now(), timeBefore);

        return duration.getSeconds();
    }

    private void prepareReminderForFuture(
        TrainingReminderRead initReminder,
        ReminderType newType,
        Principal principal,
        String timerId,
        int minutesBefore
    ) {
        var newReminder = new TrainingReminderRead(
            initReminder,
            newType
        );

        startTimerForUser(
            principal,
            timerId,
            calculateSecondsBeforeTime(
                minutesBefore,
                initReminder.getTime()
            ),
            () -> {
                log.info("Sending reminder {} min before training", minutesBefore);
                messageTemplate.convertAndSendToUser(
                    principal.getName(),
                    TrainingPlanControllerWS.NOTIFICATION_ENDPOINT,
                    newReminder
                );
            }
        );
    }

    public TrainingReminderRead getReminderAndInitRest(Authentication auth) {
        int MIN_BEFORE_NOTIFICATION = 5;
        int NOW_MIN_BEFORE = 0;

        String username = getUsernameFrom(auth);
        if (username == null)
            return null;

        var userId = UserService.getUserIdByAuth(auth);

        try {
            var initReminder = trainingPlanService.getUserTrainingReminder(
                userId,
                FIRST_REMINDER_TITLE
            );

            prepareReminderForFuture(
                initReminder,
                ReminderType.SOME_TIME_BEFORE,
                auth,
                BEFORE_TIMER_ID,
                MIN_BEFORE_NOTIFICATION
            );

            prepareReminderForFuture(
                initReminder,
                ReminderType.NOW,
                auth,
                NOW_TIMER_ID,
                NOW_MIN_BEFORE
            );

            return initReminder;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return null;
        }
    }
}
