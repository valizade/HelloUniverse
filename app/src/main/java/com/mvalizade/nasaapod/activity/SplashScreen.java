package com.mvalizade.nasaapod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mvalizade.nasaapod.R;
import com.mvalizade.nasaapod.framework.activity.MAppCompatActivity;
import com.mvalizade.nasaapod.framework.application.Base;
import com.mvalizade.nasaapod.model.Image;
import com.mvalizade.nasaapod.webservice.Call;
import com.mvalizade.nasaapod.webservice.OnResponseListener;

import java.util.List;

public class SplashScreen extends MAppCompatActivity {

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
      .load(R.drawable.jupiter)
      .into(imagSplash);
    startDelayThread();
  }

  private void getRandomImage() {
    Call.getRandomImage(new OnResponseListener() {
      @Override
      public <T> void onResponse(T object) {
        super.onResponse(object);
        Image image = (Image) object;
      }

      @Override
      public <T> void onFailure(T object) {
        super.onFailure(object);
        Log.e(Base.APP_TAG, object.toString());
      }
    });
  }

  private void getImagesList() {
    Call.getImagesList(new OnResponseListener() {
      @Override
      public <T> void onResponse(T object) {
        super.onResponse(object);
        List<Image> images = (List<Image>) object;
      }

      @Override
      public <T> void onFailure(T object) {
        super.onFailure(object);
        Log.e(Base.APP_TAG, object.toString());
      }
    });
  }

  private void goToMainActivity() {
    if(!isConnectedToInternet()) {
      showAlertDialog("ERROR",
        "Are you sure you're online?! we have problem to connet server, please check your connection and try again",
        "ok");
    } else {
      Intent intent = new Intent(SplashScreen.this, MainTest.class);
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
