package com.valizade.nasaapod.libs.base;

public interface EventBus {

  void register(Object o);
  void unregister(Object o);
  void post(Object o);
}
