package com.valizade.hellouniverse.screen.fullsizeimage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.valizade.hellouniverse.R;

public class FullSizeImage extends AppCompatActivity {

  private static final String EXTRA_IMAGE_URL = "com.valizade.hellouniverse.screen.fullsizeimage.extraImageUrl";

  public static Intent newInstance(Context packageName, String imageUrl) {
    Intent intent = new Intent(packageName, FullSizeImage.class);
    intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fullsizeimage_act);

    PhotoView photoView = findViewById(R.id.fullsizeimage_img);

    Bundle extras = getIntent().getExtras();
    String url = extras.getString(EXTRA_IMAGE_URL);

    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.loading4)
        .into(photoView);
  }
}
