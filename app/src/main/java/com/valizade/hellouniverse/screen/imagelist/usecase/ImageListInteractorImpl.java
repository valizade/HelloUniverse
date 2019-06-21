package com.valizade.hellouniverse.screen.imagelist.usecase;

import com.valizade.hellouniverse.screen.imagelist.ImageListRepository;

public class ImageListInteractorImpl implements ImageListInteractor {

  private ImageListRepository mRepository;

  public ImageListInteractorImpl(ImageListRepository repository) {
    mRepository = repository;
  }

  @Override
  public void executeGetImageList() {
    mRepository.getListImages();
  }

}
