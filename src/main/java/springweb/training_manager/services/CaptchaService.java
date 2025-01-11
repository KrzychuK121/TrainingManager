package springweb.training_manager.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@Slf4j
public class CaptchaService {
    @Value("${security.hCaptcha.secret-key}")
    private String hCaptchaSecretKey;

    private final HttpClient httpClient;
    private final ObjectMapper om = new ObjectMapper();

    public CaptchaService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }

    public boolean captchaVerified(
        String captchaToken
    ) throws IOException, InterruptedException {
        if (hCaptchaSecretKey.equals("disabled"))
            return true;
        if (captchaToken == null || captchaToken.isEmpty())
            return false;

        var sb = new StringBuilder();
        sb.append("response=");
        sb.append(captchaToken);
        sb.append("&secret=");
        sb.append(hCaptchaSecretKey);

        var request = HttpRequest.newBuilder()
            .uri(URI.create("https://hcaptcha.com/siteverify"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .timeout(Duration.ofSeconds(10))
            .POST(HttpRequest.BodyPublishers.ofString(sb.toString()))
            .build();

        HttpResponse<String> response = this.httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        JsonNode hCaptchaResponseObject = this.om.readTree(response.body());
        var success = hCaptchaResponseObject.get("success")
            .asBoolean();

        if (!success) {
            JsonNode errorCodesArray = hCaptchaResponseObject.get("error-codes");
            log.debug("Error codes found.");
            for (JsonNode errorCode : errorCodesArray)
                log.debug(" - {}", errorCode.asText());
        }

        return success;
    }
}
