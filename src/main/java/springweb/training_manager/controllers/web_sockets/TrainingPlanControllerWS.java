package springweb.training_manager.controllers.web_sockets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import springweb.training_manager.models.viewmodels.training_plan.TrainingReminderRead;
import springweb.training_manager.services.TimerServices.NotificationTimerService;
import springweb.training_manager.services.TrainingPlanService;

@RequiredArgsConstructor
@Slf4j
@RestController
public class TrainingPlanControllerWS {
    private final TrainingPlanService service;
    private final NotificationTimerService notificationTimerService;
    public static final String NOTIFICATION_ENDPOINT = "/topic/training/notification";

    @MessageMapping("/plans.reminders")
    @SendToUser(NOTIFICATION_ENDPOINT)
    public TrainingReminderRead initializeTrainingReminders(
        Authentication auth
    ) {
        return notificationTimerService.getReminderAndInitRest(auth);
    }

    /*@Scheduled(fixedRate = 20 * 1000)
    private void sendTestMessage() {
        registry.getUsers().forEach(
            simpUser -> log.info("username: {}", simpUser.getName())
        );
        log.info("----------------------------");
    }*/

}
