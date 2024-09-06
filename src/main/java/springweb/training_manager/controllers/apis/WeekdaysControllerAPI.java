package springweb.training_manager.controllers.apis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springweb.training_manager.models.entities.Weekdays;

@RestController
@RequestMapping("/api/weekdays")
public class WeekdaysControllerAPI {
    @GetMapping
    public ResponseEntity<Weekdays[]> getWeekdays(){
        return ResponseEntity.ok(Weekdays.values());
    }
}
