package com.valizade.hellouniverse.screen.imagelist;

import com.valizade.hellouniverse.api.ApodClient;
import com.valizade.hellouniverse.entities.Image;
import com.valizade.hellouniverse.libs.base.EventBus;
import com.valizade.hellouniverse.screen.imagelist.event.EventType;
import com.valizade.hellouniverse.screen.imagelist.event.ImageListEvent;
import com.valizade.hellouniverse.utils.DateUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import java.util.List;

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
    /*Call<List<Image>> call = mApodClient.getApodService()
        .getImagesList("DEMO_KEY", DateUtils.getDate(40, mLastImageDate), mLastImageDate);
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
          // We want just 30 image, because we want to have the last column full and also maybe
          // we have other video that aren't youtube video, so we get 30 for sure that we get
          // correct number of images.
          List<Image> result = images.subList(0, 30);

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
    });*/
    Observable<List<Image>> imageListObservable = mApodClient.getApodService()
        .getImagesList("DEMO_KEY", DateUtils.getDate(40, mLastImageDate), mLastImageDate);
    imageListObservable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(l -> Observable.fromIterable(l))
        .flatMap(o -> o)
        .filter(i -> (isAYoutubVideo(i.getUrl()) || i.getMediaType().equals("image")))
        .toList()
        .map(t -> {
          Collections.reverse(t);
          List<Image> result = t.subList(0, 30);
//          mLastImageDate = DateUtils.minesOneDayOfLastDate(result.get(result.size() - 1).getDate());
          return result;
        })
        .doAfterSuccess(t -> mLastImageDate = DateUtils.minesOneDayOfLastDate(t.get(t.size() - 1).getDate()))
        .toObservable()
        .subscribe(this::handleImageListResults, this::handleImageListError);
  }

  private void handleImageListResults(List<Image> images) {
    if (images != null && images.size() != 0) {
      post(images);
    } else {
      post(EventType.IMAGE_LIST_EVENT_FAILURE, "The respone is empty!");
    }
  }

  private void handleImageListError(Throwable throwable) {
    post(EventType.IMAGE_LIST_EVENT_FAILURE, throwable.getMessage());
  }


  //BuildConfig.NASA_API_KEY
  //"DEMO_KEY"
  @Override
  public void getRandomImage() {
    /*Call<List<Image>> call = mApodClient.getApodService().getRandomImage("DEMO_KEY", 4);
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
    });*/
    Observable<List<Image>> randomImageListObservable = mApodClient.getApodService()
        .getRandomImage("DEMO_KEY", 4);
    randomImageListObservable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(Observable::fromIterable)
        .flatMap(o -> o)
        .filter(f -> f.getMediaType().equals("image"))
        .toList()
        .toObservable()
        .subscribe(this::handleRandomImageResults, this::handleRandomImageRError);

  }

  private void handleRandomImageResults(List<Image> images) {
    if (images != null && images.size() != 0) {
      post(images.get(0));
    } else {
      getRandomImage();
    }
  }

  private void handleRandomImageRError(Throwable throwable) {
    post(EventType.RANDOM_IMAGE_EVENT_FAILURE, throwable.getMessage());
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
