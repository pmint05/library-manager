package com.app.librarymanager.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.Stream;
import org.bson.types.ObjectId;

public class DateUtil {

  public static final class DateFormat {

    public static final String DD_MM_YYYY = "dd/MM/yyyy";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
  }

  public static String convertToStringFrom(String objectId) {
    return dateToString(convertToDateFrom(objectId));
  }

  public static Date convertToDateFrom(String objectId) {
    return new Date(convertToTimestampFrom(objectId));
  }

  public static long convertToTimestampFrom(String objectId) {
    return Long.parseLong(objectId.substring(0, 8), 16) * 1000;
  }

  public static String dateToString(Date date) {
    return format(dateToLocalDate(date));
  }

  public static LocalDate parse(String date) {
    try {
      String[] parts = date.split("[/\\-]");
      int day = parts.length > 0 && !parts[0].isEmpty() ? Integer.parseInt(parts[0]) : 1;
      int month = parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) : 1;
      int year = parts.length > 2 ? Integer.parseInt(parts[2]) : LocalDate.now().getYear();
      return LocalDate.of(year, month, day);
    } catch (Exception e) {
      return null;
    }
  }

  public static LocalDate parse(String date, String format) {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
  }

  public static String format(LocalDate date) {
    return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
  }

  public static String ymdToDmy(String date) {
    try {
      String[] parts = date.split("-");
      return parts[2] + "/" + parts[1] + "/" + parts[0];

    } catch (ArrayIndexOutOfBoundsException e) {
      try {
        String[] parts = date.split("-");
        return parts[1] + "/" + parts[0];
      } catch (ArrayIndexOutOfBoundsException e1) {
        return date;
      }
    }
  }

  public static String format(LocalDate date, String format) {
    return date.format(DateTimeFormatter.ofPattern(format));
  }

  public static Date localDateToDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDate dateToLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static boolean isValid(String date) {
    try {
      parse(date);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isBefore(String date1, String date2) {
    return parse(date1).isBefore(parse(date2));
  }

  public static boolean isAfter(String date1, String date2) {
    return parse(date1).isAfter(parse(date2));
  }

  public static boolean isEqual(String date1, String date2) {
    return parse(date1).isEqual(parse(date2));
  }

  public static boolean isBeforeOrEqual(String date1, String date2) {
    return isBefore(date1, date2) || isEqual(date1, date2);
  }

  public static boolean isAfterOrEqual(String date1, String date2) {
    return isAfter(date1, date2) || isEqual(date1, date2);
  }

  public static boolean isBetween(String date, String start, String end) {
    return isAfterOrEqual(date, start) && isBeforeOrEqual(date, end);
  }

}
