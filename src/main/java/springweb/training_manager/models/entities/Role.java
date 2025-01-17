package springweb.training_manager.models.entities;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    ADMIN(Constants.ADMIN, "administrator"),
    MODERATOR(Constants.MODERATOR, "moderator"),
    USER(Constants.USER, "użytkownik");

    private final String authority;
    public final String display;

    Role(
        String authority,
        String display
    ) {
        this.authority = authority;
        this.display = display;
    }

    public static class Constants {
        public static final String ADMIN = "ADMIN";
        public static final String MODERATOR = "MODERATOR";
        public static final String USER = "USER";
    }
}
