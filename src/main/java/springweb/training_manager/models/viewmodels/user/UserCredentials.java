package springweb.training_manager.models.viewmodels.user;

public record UserCredentials(
    String username,
    String password,
    String captchaToken
) {
}
