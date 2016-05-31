package ch.bfh.ti.lineify.core;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static DateFormat dateFormat;

    static {
        dateFormat = DateFormat.getDateTimeInstance();
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
    }

    public static String format(Date date) {
        return dateFormat.format(date);
    }
}