package com.valizade.hellouniverse.screen.main;

import android.widget.ImageView;

import com.valizade.hellouniverse.entities.Image;

public interface ImageItemClickListener {
  void onImageClick(int pos, Image image, ImageView shareImageView);
}
