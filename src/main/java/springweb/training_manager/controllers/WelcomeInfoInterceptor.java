package springweb.training_manager.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.view_models.user.MyUserDetails;
import springweb.training_manager.services.MyUserDetailsService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WelcomeInfoInterceptor implements HandlerInterceptor {
    private final MyUserDetailsService userDetailsService;

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws Exception {
        Logger logger = LoggerFactory.getLogger(WelcomeInfoInterceptor.class);
        if (
            request.getUserPrincipal() == null ||
                request.getServletPath()
                    .contains("/api")
        )
            return HandlerInterceptor.super.preHandle(request, response, handler);

        User loggedUser = ((MyUserDetails) userDetailsService.loadUserByUsername(
            request.getUserPrincipal()
                .getName()
        )).getUser();

        var session = request.getSession();
        final String WELCOME_INFO = "welcomeInfo";

        if (session.getAttribute(WELCOME_INFO) != null)
            return HandlerInterceptor.super.preHandle(request, response, handler);
        logger.info("Setting session '" + WELCOME_INFO + "' attribute from logged user.");

        session.setAttribute(WELCOME_INFO, new String[]{loggedUser.getFirstName(), loggedUser.getLastName()});
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private static Cookie getCookieByName(String name, List<Cookie> cookies) {
        return cookies.stream()
            .filter(
                cookie -> cookie.getName()
                    .equals(name)
            )
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Nie istnieje ciasteczko o nazwie: '" + name + "'."));
    }
}
