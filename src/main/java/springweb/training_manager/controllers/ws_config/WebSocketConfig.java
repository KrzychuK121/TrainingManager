package springweb.training_manager.controllers.ws_config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import springweb.training_manager.controllers.ws_config.interceptors.TokenAuthInterceptor;
import springweb.training_manager.security.JwtService;

import java.util.ArrayList;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${security.frontend.url}")
    private String frontendUrl;
    public static final String APP_DESTINATION_PREFIX = "/websockets";
    public static final String FRONTEND_PUBLIC_ENDPOINT_PREFIX = "/topic";
    private final JwtService service;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOrigins(frontendUrl);
        registry.addEndpoint("/ws")
            .setAllowedOrigins(frontendUrl)
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(APP_DESTINATION_PREFIX);
        registry.enableSimpleBroker(FRONTEND_PUBLIC_ENDPOINT_PREFIX);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(getInterceptors());
    }

    private ChannelInterceptor[] getInterceptors() {
        ArrayList<ChannelInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new TokenAuthInterceptor(service));

        return interceptors.toArray(
            new ChannelInterceptor[interceptors.size()]
        );
    }
}
