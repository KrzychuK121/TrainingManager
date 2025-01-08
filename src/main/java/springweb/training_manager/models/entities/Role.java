package springweb.training_manager.models.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN(Constants.ADMIN),
    MODERATOR(Constants.MODERATOR),
    USER(Constants.USER);

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public static class Constants {
        public static final String ADMIN = "ADMIN";
        public static final String MODERATOR = "MODERATOR";
        public static final String USER = "USER";
    }
}
