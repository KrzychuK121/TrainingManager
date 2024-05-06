package springweb.trainingmanager.models.entities;

public enum Weekdays {
    MONDAY("poniedziałek"),
    TUESDAY("wtorek"),
    WEDNESDAY("środa"),
    THURSDAY("czwartek"),
    FRIDAY("piątek"),
    SATURDAY("sobota"),
    SUNDAY("niedziela");

    private final String display;
    private Weekdays(String display){
        this.display = display;
    }

    public static String getWeekdaysDesc(Weekdays weekdays){
        if(weekdays == null)
            return "brak";
        return weekdays.display;
    }
}
