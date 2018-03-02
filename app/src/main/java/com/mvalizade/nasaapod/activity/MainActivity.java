package com.mvalizade.nasaapod.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mvalizade.nasaapod.R;
import com.mvalizade.nasaapod.application.Base;
import com.mvalizade.nasaapod.model.Image;
import com.mvalizade.nasaapod.webservice.APIClient;
import com.mvalizade.nasaapod.webservice.APIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    Call<List<Image>> call = apiInterface.getImagesList(Base.API_KEY, "2017-07-08", "2017-07-10");
    call.enqueue(new Callback<List<Image>>() {
      @Override
      public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
        if(response.isSuccessful()) {
          List<Image> images = response.body();
          for(Image image:images){
            Log.i(Base.APP_TAG, "image title: " + image.getTitle());
          }
        } else {
          Log.i(Base.APP_TAG, "get response but an error happend");
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
        Log.i(Base.APP_TAG, "an error happend");
        Log.e(Base.APP_TAG, t.toString());
      }
    });
  }
}
