package com.valizade.hellouniverse.screen.imagelist.ui;

import android.widget.ImageView;
import com.valizade.hellouniverse.entities.Image;

public interface OnItemClickListener {

  void onClick(Image image, ImageView shareImageView);
}
