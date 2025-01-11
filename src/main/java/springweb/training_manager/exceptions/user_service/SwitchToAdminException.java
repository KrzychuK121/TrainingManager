package springweb.training_manager.exceptions.user_service;

public class SwitchToAdminException extends RuntimeException {
    public SwitchToAdminException() {
        super("Cannot switch users role to admin");
    }
}
