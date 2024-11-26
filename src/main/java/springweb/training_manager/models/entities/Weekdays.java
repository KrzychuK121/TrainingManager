package springweb.training_manager.models.entities;

import java.time.LocalDateTime;

// TODO: Change this to existing DayOfWeek enum
public enum Weekdays {
    MONDAY("poniedziałek"),
    TUESDAY("wtorek"),
    WEDNESDAY("środa"),
    THURSDAY("czwartek"),
    FRIDAY("piątek"),
    SATURDAY("sobota"),
    SUNDAY("niedziela");

    private final String display;

    Weekdays(String display) {
        this.display = display;
    }

    public static String getWeekdaysDesc(Weekdays weekdays) {
        if (weekdays == null)
            return "brak";
        return weekdays.display;
    }

    public static Weekdays getByDate(LocalDateTime date) {
        String dayString = date.getDayOfWeek()
            .toString();
        return Weekdays.valueOf(dayString);
    }
}
