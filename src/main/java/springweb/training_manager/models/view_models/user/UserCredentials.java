package springweb.training_manager.models.view_models.user;

public record UserCredentials(
    String username,
    String password,
    String captchaToken
) {
}
