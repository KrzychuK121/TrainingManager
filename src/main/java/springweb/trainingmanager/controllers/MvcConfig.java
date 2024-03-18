package springweb.trainingmanager.controllers;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private final WelcomeInfoInterceptor welcomeInfoInterceptor;

    public MvcConfig(
        final WelcomeInfoInterceptor welcomeInfoInterceptor
    ) {
        this.welcomeInfoInterceptor = welcomeInfoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(welcomeInfoInterceptor);
    }
}
