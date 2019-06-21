package com.valizade.hellouniverse.screen.imagelist;

import android.util.Log;
import androidx.annotation.NonNull;
import com.valizade.hellouniverse.api.ApodClient;
import com.valizade.hellouniverse.entities.Image;
import com.valizade.hellouniverse.libs.base.EventBus;
import com.valizade.hellouniverse.screen.imagelist.event.EventType;
import com.valizade.hellouniverse.screen.imagelist.event.ImageListEvent;
import com.valizade.hellouniverse.utils.DateUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageListRepositoryImpl implements ImageListRepository {

  private ApodClient mApodClient;
  private EventBus mEventBus;

  private String mTodayDate = DateUtils.getTodayDate();
  private String mLastImageDate = mTodayDate;

  public ImageListRepositoryImpl(ApodClient apodClient, EventBus eventBus) {
    mApodClient = apodClient;
    mEventBus = eventBus;
  }

  public boolean isAYoutubVideo(String url) {
    return url.indexOf("youtube") > 0;
  }

  @Override
  public void getListImages() {
    Call<List<Image>> call = mApodClient.getApodService()
        .getImagesList("DEMO_KEY", DateUtils.getDate(22, mLastImageDate), mLastImageDate);
    call.enqueue(new Callback<List<Image>>() {
      @Override
      public void onResponse(@NonNull Call<List<Image>> call,
          @NonNull Response<List<Image>> response) {
        if (response.isSuccessful() && response.body() != null) {
          List<Image> images = response.body();

          // Reverse the items in list
          Collections.reverse(images);

          // Remove items from list that are video and aren't youtube video
          Iterator<Image> it = images.iterator();
          while (it.hasNext()) {
            Image image = it.next();
            if (!(isAYoutubVideo(image.getUrl()) || image.getMediaType().equals("image"))) {
              it.remove();
            }

          }
          // We want just 14 image, because we want to have the last column full and also maybe
          // we have other video that aren't youtube video, so we get 14 for sure that we get
          // correct number of images.
          List<Image> result = images.subList(0, 14);

          mLastImageDate = DateUtils.minesOneDayOfLastDate(result.get(result.size() - 1).getDate());

          post(result);
        } else {
          post(EventType.IMAGE_LIST_EVENT_FAILURE, response.errorBody().toString());
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
        post(EventType.IMAGE_LIST_EVENT_FAILURE, t.getMessage());
      }
    });
  }

  @Override
  public void getRandomImage() {
    Call<List<Image>> call = mApodClient.getApodService().getRandomImage("DEMO_KEY", 4);
    call.enqueue(new Callback<List<Image>>() {
      @Override
      public void onResponse(@NonNull Call<List<Image>> call,
          @NonNull Response<List<Image>> response) {
        if (response.isSuccessful() && response.body() != null) {
          List<Image> images = response.body();
          Image resault = null;
          for (Image image : images) {
            if (image.getMediaType().equals("image")) {
              resault = image;
            }
          }
          if (resault != null) {
            post(resault);
          } else {
            // In this moment all of our recieved images are video! so we call again
            getRandomImage();
          }
        } else {
          post(EventType.RANDOM_IMAGE_EVENT_FAILURE, response.errorBody().toString());
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
        post(EventType.RANDOM_IMAGE_EVENT_FAILURE, t.getMessage());
      }
    });
  }

  private void post(List<Image> images) {
    post(EventType.IMAGE_LIST_EVENT_SUCCESS, null, null, images);
  }

  private void post(Image image) {
    post(EventType.RANDOM_IMAGE_EVENT_SUCCESS, null, image, null);
  }

  private void post(EventType eventType, String errorMessage) {
    post(eventType, errorMessage, null, null);
  }

  private void post(EventType eventType, String errorMessage, Image image, List<Image> images) {
    ImageListEvent event = new ImageListEvent();
    event.setEventType(eventType);
    event.setError(errorMessage);
    event.setImage(image);
    event.setImageList(images);
    mEventBus.post(event);
  }
}
