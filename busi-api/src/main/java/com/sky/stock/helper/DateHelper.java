package com.sky.stock.helper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateHelper {

    public static LocalDate usDate(Long timestamp) {
        ZoneId usZone = ZoneId.of("America/New_York"); // 美国东部时区
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDate localDate = instant.atZone(usZone).toLocalDate();
        return localDate;
    }

    public static LocalDate usDateNow() {
        ZonedDateTime now = ZonedDateTime.now();
        ZoneId usZone = ZoneId.of("America/New_York");
        ZonedDateTime usTime = now.withZoneSameInstant(usZone);
        LocalDate usDate = usTime.toLocalDate();
        return usDate;
    }

    public static String toStr(LocalDate date, String pattern) {
        if (pattern == null) {
            pattern = "yyyy-MM-dd";
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        String format = dateTimeFormatter.format(date);
        return format;
    }

}
