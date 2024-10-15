package springweb.training_manager.models.viewmodels.enums;

import lombok.Getter;
import springweb.training_manager.models.entities.Weekdays;

import java.util.Arrays;
import java.util.List;

@Getter
public class WeekdayRead {
    private final Weekdays weekday;
    private final String weekdayDisplay;

    public WeekdayRead(Weekdays weekday) {
        this.weekday = weekday;
        this.weekdayDisplay = Weekdays.getWeekdaysDesc(weekday);
    }

    public static List<WeekdayRead> getWeekdaysRead() {
        return Arrays.stream(Weekdays.values())
            .map(
                WeekdayRead::new
            ).toList();
    }
}
