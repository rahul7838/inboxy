package in.smslite.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

  private final static String AGO = " ago";
  private final static String PLURALIZE = "s";
  private final static String[] units = {"just now", "min", "hour", "day", "week", "month", "year"};
  private final static String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  private final static String[] months = {"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

  public static String getPrettyElapsedTime(Long timeStamp) {
    Date createdAt = new Date(timeStamp);
    Long currentTime = System.currentTimeMillis();
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timeStamp);
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
    String time = sdf.format(createdAt);
    // java.lang.ArrayIndexOutOfBoundsException: length=7; index=7 for below line when u install first time
    // it will give error at message Sync screen
//    int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
    int currentDay = createdAt.getDay();
    int currentMonth = calendar.get(Calendar.MONTH);
    int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
    int currentYear = calendar.get(Calendar.YEAR);
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
    return days[day] + " " + time;
  }

  private static String getCorrectMonth(int month, int date, String time) {
      return months[month] + " " + date + " " + time;
  }

  private static String getCorrectYear(int month, int date, int year, String time) {
      return months[month] + " " + date + ", " + year + " " + time;
  }
}
