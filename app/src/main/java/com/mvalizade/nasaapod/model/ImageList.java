package com.mvalizade.nasaapod.model;

import java.util.List;

public class ImageList {
  private List<Image> results;

  public void setResults(List<Image> results) {
    this.results = results;
  }

  public List<Image> getResults() {
    return results;
  }
}
