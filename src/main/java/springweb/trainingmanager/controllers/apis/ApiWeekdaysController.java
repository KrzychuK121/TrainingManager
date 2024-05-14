package springweb.trainingmanager.controllers.apis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springweb.trainingmanager.models.entities.Weekdays;

@RestController
@RequestMapping("/weekdays")
public class ApiWeekdaysController {
    @GetMapping
    public ResponseEntity<Weekdays[]> getWeekdays(){
        return ResponseEntity.ok(Weekdays.values());
    }
}
