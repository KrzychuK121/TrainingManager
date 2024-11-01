package springweb.training_manager.controllers.web_sockets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import springweb.training_manager.services.MyUserDetailsService;
import springweb.training_manager.services.TrainingPlanService;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final TrainingPlanService service;
    private final MyUserDetailsService userDetailsService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // TODO: Imp logic to remove all timers with queued notifications to send
    }
}
