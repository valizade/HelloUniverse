package com.valizade.hellouniverse.screen.imagelist;

import com.valizade.hellouniverse.BasePresenter;
import com.valizade.hellouniverse.BaseView;
import com.valizade.hellouniverse.entities.Image;
import com.valizade.hellouniverse.screen.imagelist.event.ImageListEvent;
import java.util.List;

public interface ImageListContract {

  interface View extends BaseView<Presenter> {

    void showUi(boolean show);

    void showMainProgressbar(boolean show);

    void setListContent(List<Image> images);

    void setHeaderContent(Image image);

    void showProgressbar(boolean show);

    void showErrorScreen(boolean show);

    void showErrorMessage(String errorMessage);
  }

  interface Presenter extends BasePresenter {

    void getImageList();

    void getHeaderImage();

    void loadMoreImage();

    void onEventMainThread(ImageListEvent evet);
  }

}
