package com.valizade.hellouniverse.screen.imagelist.usecase;

import com.valizade.hellouniverse.screen.imagelist.ImageListRepository;

public class RandomImageInteractorImpl implements RandomImageIneractor {

  private ImageListRepository mRepository;

  public RandomImageInteractorImpl(ImageListRepository repository) {
    mRepository = repository;
  }

  @Override
  public void execute() {
    mRepository.getRandomImage();
  }
}
