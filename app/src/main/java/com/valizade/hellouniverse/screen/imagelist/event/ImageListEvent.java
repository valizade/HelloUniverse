package com.valizade.hellouniverse.screen.imagelist.event;

import com.valizade.hellouniverse.entities.Image;
import java.util.List;

public class ImageListEvent {

  private EventType mEventType;
  private String error;
  private List<Image> mImageList;
  private Image mImage;

  public EventType getEventType() {
    return mEventType;
  }

  public void setEventType(EventType eventType) {
    mEventType = eventType;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public List<Image> getImageList() {
    return mImageList;
  }

  public void setImageList(List<Image> imageList) {
    mImageList = imageList;
  }

  public Image getImage() {
    return mImage;
  }

  public void setImage(Image image) {
    mImage = image;
  }
}
