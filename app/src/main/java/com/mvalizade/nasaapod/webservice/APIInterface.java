package com.mvalizade.nasaapod.webservice;

import com.mvalizade.nasaapod.model.Image;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

  @GET("planetary/apod")
  Call<List<Image>> getImagesList(
    @Query("api_key") String apiKey,
    @Query("start_date") String startDate,
    @Query("end_date") String endDate);

  @GET("planetary/apod")
  Call<List<Image>> getRandomImage(
    @Query("api_key") String apiKey,
    @Query("count") int count);
}
