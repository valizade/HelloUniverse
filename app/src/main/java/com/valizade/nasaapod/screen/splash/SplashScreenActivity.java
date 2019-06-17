package com.valizade.nasaapod.screen.splash;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.valizade.nasaapod.R;
import com.valizade.nasaapod.screen.main.MainActivity;
import com.valizade.nasaapod.utils.MAppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

  private final int SPLASH_DISPLAY_LENGTH = 2000;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splashscreen);

    ImageView imagSplash = findViewById(R.id.img_splash);

    //I Used Glide because the image show in this image view is a gif
    Glide
        .with(this)
        .load(R.drawable.jupiter)
        .into(imagSplash);

    startDelayThread();
  }

  private void startDelayThread() {
    new Handler().postDelayed(this::goToMainActivity, SPLASH_DISPLAY_LENGTH);
  }

  private void goToMainActivity() {
    Intent intent = MainActivity
        .newInstance(SplashScreenActivity.this);
    startActivity(intent);
    finish();
  }

}
