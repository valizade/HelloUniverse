package com.valizade.hellouniverse.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
  //retrun date. if pastDays=0 then return current date else return pastDays ago date.
  public static String getDate(int pastDays, String lastDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    Calendar cal = GregorianCalendar.getInstance();
    setCalenderTime(cal, dateFormat, lastDate);
    cal.add(Calendar.DAY_OF_YEAR, -pastDays);
    Date date = cal.getTime();
    return dateFormat.format(date);
  }

  public static void setCalenderTime(Calendar cal, DateFormat dateFormat, String lastDate) {
    if(lastDate != null && !lastDate.equals("")){
      try {
        cal.setTime(dateFormat.parse(lastDate));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else {
      cal.setTime(new Date());
    }
  }

  public static String getTodayDate() {
    return getDate(0, null);
  }

  public static String minesOneDayOfLastDate(String date) { return getDate(1, date); }

  public static String plusOneDay(String date) {
    return getDate(-1, date);
  }
}
