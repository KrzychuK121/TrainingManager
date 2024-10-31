package springweb.training_manager.controllers.ws_config.interceptors;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import springweb.training_manager.security.JwtService;

@RequiredArgsConstructor
public class TokenAuthInterceptor implements ChannelInterceptor {
    private final JwtService service;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
            message,
            StompHeaderAccessor.class
        );

        if (!StompCommand.CONNECT.equals(accessor.getCommand()))
            return message;

        Authentication user = service.getToken(
            accessor.getFirstNativeHeader(
                JwtService.AUTH_HEADER
            )
        );

        accessor.setUser(user);
        return message;
    }
}
