package com.valizade.nasaapod.screen.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.valizade.nasaapod.R;
import com.valizade.nasaapod.utils.MAppCompatActivity;
import com.valizade.nasaapod.Base;
import com.valizade.nasaapod.screen.main.MainActivity;

public class SplashScreenActivity extends MAppCompatActivity {

  /** Duration of wait **/
  private final int SPLASH_DISPLAY_LENGTH = 5000;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splashscreen);
    ImageView imagSplash = findViewById(R.id.img_splash);
    Glide
      .with(this)
      .load(R.drawable.loading4)
      .into(imagSplash);
    startDelayThread();
  }

  private void goToMainActivity() {
    if(!isConnectedToInternet()) {
      showAlertDialog("ERROR",
        "Are you sure you're online?! we have problem to connet server, please check your connection and try again",
        "ok");
    } else {
      Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
      startActivity(intent);
      finish();
    }
  }

  private void startDelayThread() {
    Log.i(Base.APP_TAG, "startDelayThread() start");
    new Handler().postDelayed(new Runnable(){
      @Override
      public void run() {
        Log.i(Base.APP_TAG, "startDelayThread() finish");
        goToMainActivity();
      }
    }, SPLASH_DISPLAY_LENGTH);
  }

}
