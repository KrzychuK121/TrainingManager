package springweb.training_manager.models.viewmodels.enums;

import lombok.Getter;
import springweb.training_manager.models.entities.Weekdays;

@Getter
public class WeekdayRead {
    private final Weekdays weekday;
    private final String weekdayDisplay;

    public WeekdayRead(Weekdays weekday) {
        this.weekday = weekday;
        this.weekdayDisplay = Weekdays.getWeekdaysDesc(weekday);
    }
}
