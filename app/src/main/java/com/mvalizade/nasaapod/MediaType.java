package com.mvalizade.nasaapod;

public enum MediaType {
  IMAGE, VIDEO;

  @Override
  public String toString() {
    switch (this) {
      case IMAGE : return "image";
      case VIDEO : return "video";
      default : return super.toString();
    }
  }
}
