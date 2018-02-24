package in.smslite.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

  private final static String AGO = " ago";
  private final static String PLURALIZE = "s";
  private final static String[] units = {"just now", "min", "hour", "day", "week", "month", "year"};
  private final static String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  private final static String[] months = {"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

  public static String getPrettyElapsedTime(Date createdAt) {
    Long currentTime = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
    String time = sdf.format(createdAt);
    int currentDay = createdAt.getDay();
    int currentMonth = createdAt.getMonth();
    int currentDate = createdAt.getDate();
    int currentYear = createdAt.getYear();
    Long elapsedTimeSecs = (currentTime - createdAt.getTime()) / 1000;
    if (elapsedTimeSecs < 60) {
      return units[0];
    } else if (elapsedTimeSecs < 3600) {
      return getCorrectUnit(elapsedTimeSecs / 60, units[1]);
    } else if (elapsedTimeSecs < 86400) {
      return getCorrectUnit(elapsedTimeSecs / 3600, units[2]);
    } else if (elapsedTimeSecs < 604800) {
      return getCorrectDay(currentDay, time);
    } else if (elapsedTimeSecs < 2592000) {
      return getCorrectMonth(currentMonth, currentDate, time);
    } else if (elapsedTimeSecs < 31536000) {
      return getCorrectMonth(currentMonth,currentDate,time);
    } else {
      return getCorrectYear(currentMonth,currentDate,currentYear,time);
    }
  }

  private static String getCorrectUnit(Long number, String unit) {
    if (number > 1) {
      return number.toString() + " " + unit + PLURALIZE + AGO;
    } else {
      return number.toString() + " " + unit + AGO;
    }
  }

  private static String getCorrectDay(int day, String time) {
    return days[day]+ " "+ String.valueOf(time);
  }

  private static String getCorrectMonth(int month, int date, String time) {
    return months[month] + "," + String.valueOf(date) +" "+ String.valueOf(time);
  }

  private static String getCorrectYear(int month, int date, int year, String time) {
    return months[month]+ "," + String.valueOf(date)+ "," + String.valueOf(year)+ " "+ String.valueOf(time);
  }
}
