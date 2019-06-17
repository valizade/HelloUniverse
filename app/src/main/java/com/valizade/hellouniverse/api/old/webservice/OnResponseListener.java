package com.valizade.hellouniverse.api.old.webservice;

public abstract class OnResponseListener {
  public <T> void onResponse(T object) {}
  public <T> void onFailure(T object) {}
}
