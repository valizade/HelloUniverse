package com.valizade.nasaapod.webservice;

public abstract class OnResponseListener {
  public <T> void onResponse(T object) {};
  public <T> void onFailure(T object) {};
}
