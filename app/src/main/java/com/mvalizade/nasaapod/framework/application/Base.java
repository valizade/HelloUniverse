package com.mvalizade.nasaapod.framework.application;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Base extends Application {

  public final static String API_KEY = "8KdOoezpUk08lJCZ42treSWRTxTYyxIUjvvH0Ibk";
  public final static String API_KEY_DEMO = "DEMO_KEY";
  public final static String APP_TAG = "nasa_apod_tag";
  public final static int STATE_PROGRESS_DONE = 1;
  public final static int STATE_IN_PROGRESS = 2;
  public final static int STATE_ERROR = 3;
  public final static int API_STATE_LIST_IMAGE = 2;
  public final static int API_STATE_RANDOM_IMAGE = 1;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  //retrun date. if pastDays=0 then return current date else return pastDays ago date.
  public static String getDate(int pastDays) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DAY_OF_YEAR, -pastDays);
    Date date = cal.getTime();
    return dateFormat.format(date);
  }

  //this function get a youtube url and return video id of this url
  public static String extractYoutubeId(String url) {
    String[] param = url.split("https://www.youtube.com/embed/");
    String[] param2 = param[1].split("\\?rel=0");
    return param2[0];
  }

}
