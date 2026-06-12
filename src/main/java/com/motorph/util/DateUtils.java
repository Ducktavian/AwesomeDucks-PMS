package com.motorph.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateUtils {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    
    public static String dateToString(LocalDate date) {
        return (date == null) ? "" : date.format(FORMATTER);
    }
    
    public static String timeToString(LocalTime time) {
        return (time == null) ? "" : time.format(TIME_FORMAT);
    }
    
    public static LocalDate stringToLocalDate(String dateStr) {
        return (dateStr == null || dateStr.isEmpty()) ? null : LocalDate.parse(dateStr, FORMATTER);
    }
    
    public static LocalTime stringToTime(String timeStr) {
        return (timeStr == null || timeStr.isEmpty()) ? null : LocalTime.parse(timeStr, TIME_FORMAT);
    }
    
    // Used in Jcalendar formatting
    public static String convertDateToString(Date date) {
        if (date == null) return "";

        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return localDate.format(FORMATTER);
    }
    
    // Used in Jcalendar formatting
    public static LocalDate convertDateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        // Convert the Calendar to an Instant, apply the ZoneId, and extract the LocalDate
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        return localDate;
    }

    // Used in Jcalendar formatting
    public static Date stringToUtilDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        
        LocalDate localDate = LocalDate.parse(dateStr, FORMATTER);
        
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    // Used in Jcalendar formatting
    public static Date localDateToUtilDate(LocalDate localDate) {
        if (localDate == null) return null;
        
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    }
}
