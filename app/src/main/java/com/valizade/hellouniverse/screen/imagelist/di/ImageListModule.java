package com.valizade.hellouniverse.screen.imagelist.di;

import com.valizade.hellouniverse.api.ApodClient;
import com.valizade.hellouniverse.libs.base.EventBus;
import com.valizade.hellouniverse.libs.base.ImageLoader;
import com.valizade.hellouniverse.libs.di.LibraryModule;
import com.valizade.hellouniverse.screen.imagelist.ImageListContract;
import com.valizade.hellouniverse.screen.imagelist.ImageListPresenter;
import com.valizade.hellouniverse.screen.imagelist.ImageListRepository;
import com.valizade.hellouniverse.screen.imagelist.ImageListRepositoryImpl;
import com.valizade.hellouniverse.screen.imagelist.ui.ImageListFragment;
import com.valizade.hellouniverse.screen.imagelist.ui.OnImageListLoadListener;
import com.valizade.hellouniverse.screen.imagelist.ui.OnItemClickListener;
import com.valizade.hellouniverse.screen.imagelist.ui.adapter.ImageListAdapter;
import com.valizade.hellouniverse.screen.imagelist.usecase.ImageListInteractor;
import com.valizade.hellouniverse.screen.imagelist.usecase.ImageListInteractorImpl;
import com.valizade.hellouniverse.screen.imagelist.usecase.RandomImageIneractor;
import com.valizade.hellouniverse.screen.imagelist.usecase.RandomImageInteractorImpl;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class ImageListModule {

  ImageListContract.View mView;
  OnItemClickListener mClickListener;
  OnImageListLoadListener mImageListLoadListener;

  public ImageListModule(ImageListContract.View view, OnItemClickListener clickListener, OnImageListLoadListener imageListLoadListener) {
    mView = view;
    mClickListener = clickListener;
    mImageListLoadListener = imageListLoadListener;
  }

  @Singleton
  @Provides
  OnImageListLoadListener providesOnImageListLoadListener() {
    return mImageListLoadListener;
  }

  @Singleton
  @Provides
  ImageListContract.View providesView() {
    return mView;
  }

  @Singleton
  @Provides
  OnItemClickListener provideOnItemClickListener() {
    return mClickListener;
  }

  @Singleton
  @Provides
  ImageListAdapter providesImageListAdapter(@Named("listImageLoader") ImageLoader imageLoader) {
    return new ImageListAdapter(new ArrayList<>(0), imageLoader, mClickListener, mImageListLoadListener);
  }

  @Singleton
  @Provides
  ImageListContract.Presenter providesImageListPresenter(ImageListInteractor imageListInteractor,
      RandomImageIneractor randomImageIneractor, EventBus eventBus) {
    return new ImageListPresenter(mView, imageListInteractor, randomImageIneractor, eventBus);
  }

  @Singleton
  @Provides
  ImageListInteractor providesImageListInteractor(ImageListRepository repository) {
    return new ImageListInteractorImpl(repository);
  }

  @Singleton
  @Provides
  RandomImageIneractor providesRandomImageIneractor(ImageListRepository repository) {
    return new RandomImageInteractorImpl(repository);
  }

  @Singleton
  @Provides
  ImageListRepository providesImageListRepository(ApodClient apodClient, EventBus eventBus) {
    return new ImageListRepositoryImpl(apodClient, eventBus);
  }

  @Singleton
  @Provides
  ApodClient providesApodClient() {
    return new ApodClient();
  }

}
