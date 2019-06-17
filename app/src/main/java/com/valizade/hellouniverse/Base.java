package com.valizade.hellouniverse;

import android.app.Application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Base extends Application {

  public final static String API_KEY_DEMO = "DEMO_KEY";
  public final static String APP_TAG = "nasa_apod_tag";
  public final static String CALL_GET_RANDOM_IMAGE = "get_random_image";
  public final static String CALL_GET_IMAGES_LIST= "get_images_list";
  public final static int STATE_PROGRESS_DONE = 1;
  public final static int STATE_IN_PROGRESS = 2;
  public final static int STATE_ERROR = 3;
  public final static int STATE_GET_IMAGES_SUCCESSFUL = 1;
  public final static int STATE_GET_IMAGES_FAILED = 2;
  public final static int STATE_BUG_ERROR_HEADER = 1;
  public final static int STATE_BUG_ERROR_LIST = 2;
  public final static int STATE_CONNECTION_ERROR_HEADER = 3;
  public final static int STATE_CONNECTION_ERROR_LIST = 4;
  public final static int API_STATE_LIST_IMAGE = 2;
  public final static int API_STATE_RANDOM_IMAGE = 1;
  public final static int HANDLER_DELAY = 2000;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  //retrun date. if pastDays=0 then return current date else return pastDays ago date.
  public static String getDate(int pastDays, String lastDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

  //this function get a youtube url and return video id of this url
  public static String extractYoutubeId(String url) {
    String[] param = url.split("https://www.youtube.com/embed/");
    String[] param2 = param[1].split("\\?rel=0");
    return param2[0];
  }

  public static boolean isAYoutubVideo(String url) {
    return url.indexOf("youtube") > 0;
  }

}
