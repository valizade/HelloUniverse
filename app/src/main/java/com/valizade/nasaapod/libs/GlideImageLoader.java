package com.valizade.nasaapod.libs;

import android.widget.ImageView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.valizade.nasaapod.libs.base.ImageLoader;

public class GlideImageLoader implements ImageLoader {

  private RequestManager mRequestManager;
  private RequestListener mRequestListener;

  public GlideImageLoader(RequestManager requestManager) {
    mRequestManager = requestManager;
  }

  @Override
  public void setOnFinishLoadingImageListener(Object listener) {
    if (listener instanceof RequestListener) {
      mRequestListener = (RequestListener) listener;
    }
  }

  @Override
  public void load(ImageView imageView, String url) {
    if (mRequestListener != null) {
      mRequestManager.load(url)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .centerCrop()
          .listener(mRequestListener)
          .into(imageView);
    } else {
      mRequestManager.load(url)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .centerCrop()
          .into(imageView);
    }
  }
}
