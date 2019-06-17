package com.valizade.hellouniverse.libs.base;

import android.widget.ImageView;

public interface ImageLoader {

  void load(ImageView imageView, String url);
  void setOnFinishLoadingImageListener(Object listener);
}
