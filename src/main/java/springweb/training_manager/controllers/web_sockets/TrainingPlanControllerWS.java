package springweb.training_manager.controllers.web_sockets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import springweb.training_manager.models.viewmodels.training_plan.TrainingReminderRead;
import springweb.training_manager.services.TrainingPlanService;
import springweb.training_manager.services.UserService;

@RequiredArgsConstructor
@Slf4j
@RestController
public class TrainingPlanControllerWS {
    private final TrainingPlanService service;
    public static final String NOTIFICATION_ENDPOINT = "/topic/training/notification";
    public static final String FIRST_REMINDER_TITME = "Przypomnienie o treningu na dziś";

    @MessageMapping("/plans.reminders")
    @SendToUser(NOTIFICATION_ENDPOINT)
    public TrainingReminderRead initializeTrainingReminders(
        SimpMessageHeaderAccessor accessor,
        Authentication auth
    ) {
        String username = accessor.getUser() != null
            ? accessor.getUser().getName()
            : null;
        if (username == null)
            return null;

        var userId = UserService.getUserIdByAuth(auth);

        log.info("Sending reminder");
        try {
            return service.getUserTrainingReminder(
                userId,
                FIRST_REMINDER_TITME
            );
        } catch (IllegalStateException ignore) {
            return null;
        }

    }

    /*@Scheduled(fixedRate = 20 * 1000)
    private void sendTestMessage() {
        registry.getUsers().forEach(
            simpUser -> log.info("username: {}", simpUser.getName())
        );
        log.info("----------------------------");
    }*/

}
