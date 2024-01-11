package logalyzes.server.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {


    public  static String getStringDate() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
