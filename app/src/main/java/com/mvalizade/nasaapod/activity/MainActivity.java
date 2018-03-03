package com.mvalizade.nasaapod.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.mvalizade.nasaapod.R;
import com.mvalizade.nasaapod.adapter.ImageAdapter;
import com.mvalizade.nasaapod.framework.activity.MActivity;
import com.mvalizade.nasaapod.framework.application.Base;
import com.mvalizade.nasaapod.model.Image;
import com.mvalizade.nasaapod.webservice.APIClient;
import com.mvalizade.nasaapod.webservice.APIInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends MActivity {
  
  RecyclerView recyclerView;
  ImageAdapter adapter;
  List<Image> imageList;
  ProgressBar progressBar;

  String currentDate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    progressBar = findViewById(R.id.progressBar);
    recyclerView = findViewById(R.id.recycler_view);

    handleUi(Base.STATE_IN_PROGRESS);

    //Log.i(Base.APP_TAG, "##################### time is: " + Base.getPastDate(30));

    //initialize appbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    initCollapsingToolbar(getString(R.string.app_name), R.id.collapsing_toolbar, R.id.appbar);
    try {
      Glide.with(this).load(R.drawable.ic_launcher_background).into((ImageView) findViewById(R.id.backdrop));
    } catch (Exception e) {
      e.printStackTrace();
    }

    //initialize recyclerview
    imageList = new ArrayList<>();
    adapter = new ImageAdapter(this, imageList);
    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);

    initApi();

  }


  private void initApi() {
    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    Call<List<Image>> call = apiInterface.getImagesList(Base.API_KEY, Base.getDate(15), Base.getDate(0));
    call.enqueue(new Callback<List<Image>>() {
      @Override
      public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
        if(response.isSuccessful()) {
          List<Image> images = response.body();

          Collections.reverse(images);

          for(Image image:images){
            Log.i(Base.APP_TAG, "type : " + image.getMediaType());
            if( image.getMediaType().equals("image")) {
              imageList.add(new Image(image.getTitle(), image.getDate(), image.getUrl(), image.getMediaType()));
            }
          }
          adapter.notifyDataSetChanged();
          handleUi(Base.STATE_PROGRESS_DONE);
        } else {
          Log.i(Base.APP_TAG, "get response but an error happend");
          handleUi(Base.STATE_ERROR);
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<Image>> call, @NonNull Throwable t) {
        Log.i(Base.APP_TAG, "an error happend");
        Log.e(Base.APP_TAG, t.toString());
        handleUi(Base.STATE_ERROR);
      }
    });
  }

  //handle ui with visibility of progressBar and recyclerView
  private void handleUi(int state) {
    switch (state) {
      case Base.STATE_IN_PROGRESS:
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        break;

      case Base.STATE_PROGRESS_DONE:
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        break;

      case Base.STATE_ERROR:
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        break;
    }
  }
}
