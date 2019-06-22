package com.valizade.hellouniverse;

import android.app.Activity;
import android.app.Application;
import com.valizade.hellouniverse.libs.di.LibraryModule;
import com.valizade.hellouniverse.screen.imagelist.ImageListContract;
import com.valizade.hellouniverse.screen.imagelist.di.DaggerImageListComponent;
import com.valizade.hellouniverse.screen.imagelist.di.ImageListComponent;
import com.valizade.hellouniverse.screen.imagelist.di.ImageListModule;
import com.valizade.hellouniverse.screen.imagelist.ui.OnImageListLoadListener;
import com.valizade.hellouniverse.screen.imagelist.ui.OnItemClickListener;

public class Base extends Application {

  //this function get a youtube url and return video id of this url
  public static String extractYoutubeId(String url) {
    String[] param = url.split("https://www.youtube.com/embed/");
    String[] param2 = param[1].split("\\?rel=0");
    return param2[0];
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  public ImageListComponent getImageListComponent(Activity activity, ImageListContract.View view,
      OnItemClickListener clickListener, OnImageListLoadListener imageListLoadListener) {
    return DaggerImageListComponent
        .builder()
        .libraryModule(new LibraryModule(activity))
        .imageListModule(new ImageListModule(view, clickListener, imageListLoadListener))
        .build();
  }


}
