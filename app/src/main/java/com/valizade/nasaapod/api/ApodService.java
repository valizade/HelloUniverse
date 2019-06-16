package com.valizade.nasaapod.api;

import com.valizade.nasaapod.entities.Image;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApodService {

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
