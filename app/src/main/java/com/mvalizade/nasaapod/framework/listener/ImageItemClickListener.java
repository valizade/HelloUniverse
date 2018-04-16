package com.mvalizade.nasaapod.framework.listener;

import android.widget.ImageView;

import com.mvalizade.nasaapod.model.Image;

public interface ImageItemClickListener {
  void onImageClick(int pos, Image image, ImageView shareImageView);
}
