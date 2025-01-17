package springweb.training_manager.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (!request.getServletPath()
            .contains("/api")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(JwtService.AUTH_HEADER);

        try {
            UsernamePasswordAuthenticationToken authToken = jwtService.getToken(authHeader);

            if (authToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            authToken.setDetails(
                new WebAuthenticationDetailsSource()
                    .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                .setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (LockedException ex) {
            response.sendError(HttpStatus.LOCKED.value(), ex.getMessage());
        } catch (ExpiredJwtException ex) {
            filterChain.doFilter(request, response);
        }
    }
}
