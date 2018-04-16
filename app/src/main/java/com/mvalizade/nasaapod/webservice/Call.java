package com.mvalizade.nasaapod.webservice;

import android.support.annotation.NonNull;
import android.util.Log;

import com.mvalizade.nasaapod.MediaType;
import com.mvalizade.nasaapod.framework.application.Base;
import com.mvalizade.nasaapod.model.Image;

import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class Call {

  private static retrofit2.Call<List<Image>> call;
  private static APIInterface apiInterface;
  private static List<Image> images;

  public static void getRandomImage(final OnResponseListener onResponseListener) {
    Log.i(Base.APP_TAG, "getRandomImage start");
    apiInterface = APIClient.getClient().create(APIInterface.class);
    call = apiInterface.getRandomImage(Base.API_KEY_DEMO, 4);
    call.enqueue(new Callback<List<Image>>() {
      @Override
      public void onResponse(@NonNull retrofit2.Call<List<Image>> call, @NonNull Response<List<Image>> response) {
        if(response.isSuccessful()) {
          Log.i(Base.APP_TAG, "successfully get a random image...");
          clearList(images);
          images = response.body();
          Image image = getValidImage();
          if(image != null) {
            onResponseListener.onResponse(image);
          } else {
            Log.i(Base.APP_TAG, "image that get from nasa not realy an image! i try again and get a real image");
            getRandomImage(onResponseListener);
          }
        }
      }

      @Override
      public void onFailure(@NonNull retrofit2.Call<List<Image>> call, @NonNull Throwable t) {
        Log.i(Base.APP_TAG, "while getting random image an error happend");
        Log.e(Base.APP_TAG, t.toString());
        onResponseListener.onFailure(t);
      }
    });
  }

  public static void getImagesList(final OnResponseListener onResponseListener, String lastDate) {
    Log.i(Base.APP_TAG, "getImagesList start");
    apiInterface = APIClient.getClient().create(APIInterface.class);
    call = apiInterface.getImagesList(Base.API_KEY, Base.getDate(15, lastDate), lastDate);
    call.enqueue(new Callback<List<Image>>() {
      @Override
      public void onResponse(@NonNull retrofit2.Call<List<Image>> call, @NonNull Response<List<Image>> response) {
        if(response.isSuccessful()) {
          Log.i(Base.APP_TAG, "successfully get images list...");
          clearList(images);
          images = response.body();
          onResponseListener.onResponse(images);
        }
      }

      @Override
      public void onFailure(@NonNull retrofit2.Call<List<Image>> call, @NonNull Throwable t) {
        Log.i(Base.APP_TAG, "while getting image list an error happend");
        Log.e(Base.APP_TAG, t.toString());
        onResponseListener.onFailure(t);
      }
    });
  }

  public static void clearList(List list) {
    if(list != null) {
      list.clear();
    }
  }

  private static Image getValidImage() {
    for(Image image : images) {
      if(image.getMediaType().equals(MediaType.IMAGE.toString())){
        return image;
      }
    }
    return null;
  }

  private static String getDate() {
    String todayDate = Base.getTodayDate();
    Log.i(Base.APP_TAG, "today date is: ‌" + todayDate);
    return  todayDate;
  }
}
