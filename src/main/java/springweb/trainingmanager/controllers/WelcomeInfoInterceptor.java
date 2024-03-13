package springweb.trainingmanager.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import springweb.trainingmanager.security.SecurityConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class WelcomeInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws Exception {
        Logger logger = LoggerFactory.getLogger(WelcomeInfoInterceptor.class);
        var session = request.getSession();
        if(request.getCookies() == null)
            return HandlerInterceptor.super.preHandle(request, response, handler);
        var cookies = List.of(request.getCookies());
        final String WELCOME_INFO = "welcomeInfo";

        if(
            session.getAttribute(WELCOME_INFO) != null ||
            cookies.stream().noneMatch(
                cookie -> cookie.getName().equals(SecurityConfig.WELCOME_FIRST_NAME)
            )  ||
            cookies.stream().noneMatch(
                cookie -> cookie.getName().equals(SecurityConfig.WELCOME_LAST_NAME)
            )
        )
            return HandlerInterceptor.super.preHandle(request, response, handler);
        logger.info("Setting session " + WELCOME_INFO +  " attribute from cookies.");

        Cookie firstNameCookie = getCookieByName(SecurityConfig.WELCOME_FIRST_NAME, cookies);
        Cookie lastNameCookie = getCookieByName(SecurityConfig.WELCOME_LAST_NAME, cookies);

        session.setAttribute(WELCOME_INFO, new String[]{ firstNameCookie.getValue(), lastNameCookie.getValue() });
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private static Cookie getCookieByName(String name, List<Cookie> cookies){
        return cookies.stream().filter(
            cookie -> cookie.getName().equals(name)
        ).findAny()
        .orElseThrow(() -> new IllegalArgumentException("Nie istnieje cookie o nazwie: " + name));
    }
}
