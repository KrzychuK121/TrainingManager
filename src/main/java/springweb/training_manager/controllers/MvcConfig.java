package springweb.training_manager.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
    @Value("${security.frontend.url}")
    private String frontendUrl;
    
    private final WelcomeInfoInterceptor welcomeInfoInterceptor;
    private final String[] ALLOWED_METHODS = {
        "GET", "POST", "PUT", "PATCH", "DELETE"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(welcomeInfoInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(frontendUrl)
            .allowedMethods(ALLOWED_METHODS);
    }
}
