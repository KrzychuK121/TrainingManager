package springweb.training_manager.services;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class TimeService {
    public static LocalTime parseTime(String time) {
        if (time == null || time.isEmpty())
            return null;
        return LocalTime.parse(time);
    }
}
