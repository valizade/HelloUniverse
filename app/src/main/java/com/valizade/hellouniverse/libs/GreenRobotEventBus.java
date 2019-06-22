package com.valizade.hellouniverse.libs;

import com.valizade.hellouniverse.libs.base.EventBus;

public class GreenRobotEventBus implements EventBus {

  private org.greenrobot.eventbus.EventBus mEventBus;

  public GreenRobotEventBus(org.greenrobot.eventbus.EventBus eventBus) {
    mEventBus = eventBus;
  }

  @Override
  public void register(Object o) {
    mEventBus.register(o);
  }

  @Override
  public void unregister(Object o) {
    mEventBus.unregister(o);
  }

  @Override
  public void post(Object o) {
    mEventBus.post(o);
  }
}
