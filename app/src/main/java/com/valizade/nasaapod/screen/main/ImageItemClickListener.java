package com.valizade.nasaapod.screen.main;

import android.widget.ImageView;

import com.valizade.nasaapod.entities.Image;

public interface ImageItemClickListener {
  void onImageClick(int pos, Image image, ImageView shareImageView);
}
