package com.mvalizade.nasaapod.framework.application;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Base extends Application {

  public final static String API_KEY = "8KdOoezpUk08lJCZ42treSWRTxTYyxIUjvvH0Ibk";
  public final static String APP_TAG = "nasa_apod_tag";
  public final static int STATE_PROGRESS_DONE = 1;
  public final static int STATE_IN_PROGRESS = 2;
  public final static int STATE_ERROR = 3;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  public static String getDate(int pastDays) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DAY_OF_YEAR, -pastDays);
    Date date = cal.getTime();
    return dateFormat.format(date);
  }
}
