package com.valizade.nasaapod.framework.listener;

import android.widget.ImageView;

import com.valizade.nasaapod.model.Image;

public interface ImageItemClickListener {
  void onImageClick(int pos, Image image, ImageView shareImageView);
}
