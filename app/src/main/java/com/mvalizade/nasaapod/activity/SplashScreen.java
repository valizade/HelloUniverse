package com.mvalizade.nasaapod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mvalizade.nasaapod.R;
import com.mvalizade.nasaapod.framework.activity.MAppCompatActivity;

public class SplashScreen extends MAppCompatActivity {

  /** Duration of wait **/
  private final int SPLASH_DISPLAY_LENGTH = 3000;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.splashscreen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
    new Handler().postDelayed(new Runnable(){
      @Override
      public void run() {
                /* Create an Intent that will start the Menu-Activity. */
        Intent mainIntent = new Intent(SplashScreen.this, MainTest.class);
        SplashScreen.this.startActivity(mainIntent);
        SplashScreen.this.finish();
      }
    }, SPLASH_DISPLAY_LENGTH);
  }
}