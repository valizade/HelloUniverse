package com.mvalizade.nasaapod.application;

import android.app.Application;

public class Base extends Application {

  public final static String API_KEY = "8KdOoezpUk08lJCZ42treSWRTxTYyxIUjvvH0Ibk";
  public final static String APP_TAG = "nasa_apod_tag";

  @Override
  public void onCreate() {
    super.onCreate();
  }
}
