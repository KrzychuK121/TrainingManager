package springweb.training_manager.controllers.apis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Weekdays;
import springweb.training_manager.models.view_models.enums.WeekdayRead;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enum")
public class EnumControllerAPI {
    @GetMapping("/weekdays")
    public ResponseEntity<Weekdays[]> getWeekdays() {
        return ResponseEntity.ok(Weekdays.values());
    }

    @GetMapping("/weekdays/read")
    public ResponseEntity<List<WeekdayRead>> getWeekdaysReadList() {
        return ResponseEntity.ok(WeekdayRead.getWeekdaysRead());
    }

    @GetMapping("/body-part/read")
    public ResponseEntity<Map<BodyPart, String>> getBodyPartReadMap() {
        Map<BodyPart, String> bodyPartsMap = Arrays.stream(BodyPart.values())
            .collect(
                Collectors.toMap(
                    bodyPart -> bodyPart,
                    BodyPart::getBodyDesc
                )
            );

        return ResponseEntity.ok(bodyPartsMap);
    }

}
