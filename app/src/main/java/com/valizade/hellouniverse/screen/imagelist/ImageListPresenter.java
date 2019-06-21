package com.valizade.hellouniverse.screen.imagelist;

import com.valizade.hellouniverse.libs.base.EventBus;
import com.valizade.hellouniverse.screen.imagelist.ImageListContract.View;
import com.valizade.hellouniverse.screen.imagelist.event.ImageListEvent;
import com.valizade.hellouniverse.screen.imagelist.usecase.ImageListInteractor;
import com.valizade.hellouniverse.screen.imagelist.usecase.RandomImageIneractor;
import org.greenrobot.eventbus.Subscribe;

public class ImageListPresenter implements ImageListContract.Presenter {

  private String isListGet;
  private String isHeaderGet;

  private boolean isFirstTime = true;

  private ImageListContract.View mView;
  private ImageListInteractor mImageListInteractor;
  private RandomImageIneractor mRandomImageIneractor;
  private EventBus mEventBus;

  public ImageListPresenter(View view, ImageListInteractor imageListInteractor,
      RandomImageIneractor randomImageIneractor, EventBus eventBus) {
    mView = view;
    mImageListInteractor = imageListInteractor;
    mRandomImageIneractor = randomImageIneractor;
    mEventBus = eventBus;
  }

  @Override
  public void onResume() {
    mEventBus.register(this);
  }

  @Override
  public void onPause() {
    mEventBus.unregister(this);
  }

  @Override
  public void onDestroy() {
    mView = null;
  }

  @Override
  public void getImageList() {
    if (mView != null) {
      mView.showUi(false);
      mView.showMainProgressbar(true);
    }
    mImageListInteractor.executeGetImageList();
  }

  @Override
  public void getHeaderImage() {
    mRandomImageIneractor.execute();
  }

  @Override
  public void loadMoreImage() {
    if (mView != null) {
      mView.showProgressbar(true);
    }
    mImageListInteractor.executeGetImageList();
  }

  private void showUi() {
    if (isHeaderGet.equals("true") && isListGet.equals("true")) {
      mView.showMainProgressbar(false);
      mView.showUi(true);
      isFirstTime = false;
    } else if (isHeaderGet.equals("false") || isListGet.equals("false")) {
      mView.showMainProgressbar(false);
      mView.showErrorScreen(true);
      isFirstTime = false;
    }
  }

  @Subscribe
  @Override
  public void onEventMainThread(ImageListEvent event) {
    if (mView != null) {
      mView.showProgressbar(false);
      switch (event.getEventType()) {
        case IMAGE_LIST_EVENT_SUCCESS:
          mView.setListContent(event.getImageList());
          isListGet = "true";
          break;
        case IMAGE_LIST_EVENT_FAILURE:
          if (!isFirstTime) {
            mView.showErrorMessage(event.getError());
          }
          isListGet = "false";
          break;
        case RANDOM_IMAGE_EVENT_SUCCESS:
          mView.setHeaderContent(event.getImage());
          isHeaderGet = "true";
          break;
        case RANDOM_IMAGE_EVENT_FAILURE:
          isHeaderGet = "false";
          break;
      }

      if (isFirstTime) {
        showUi();
      }
    }
  }
}
