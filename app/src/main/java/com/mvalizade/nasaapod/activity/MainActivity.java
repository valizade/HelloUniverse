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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mvalizade.nasaapod.R;
import com.mvalizade.nasaapod.adapter.ImageAdapter;
import com.mvalizade.nasaapod.framework.activity.MAppCompatActivity;
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

public class MainActivity extends MAppCompatActivity {
  
  private RecyclerView recyclerView;
  private ImageAdapter adapter;
  private List<Image> imageList;
  private ProgressBar progressBar;
  private Call<List<Image>> call;
  private APIInterface apiInterface;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    progressBar = findViewById(R.id.progressBar);
    recyclerView = findViewById(R.id.recycler_view);

    handleUi(Base.STATE_IN_PROGRESS);

    //initialize appbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    initCollapsingToolbar(getString(R.string.app_name), R.id.collapsing_toolbar, R.id.appbar);
    getImage(Base.API_STATE_RANDOM_IMAGE);

    //initialize recyclerview
    imageList = new ArrayList<>();
    adapter = new ImageAdapter(this, imageList);
    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);

    getImage(Base.API_STATE_LIST_IMAGE);

  }

  private void getImage(final int state) {
    apiInterface = APIClient.getClient().create(APIInterface.class);
    setCall(state);
    call.enqueue(new Callback<List<Image>>() {
      @Override
      public void onResponse(@NonNull Call<List<Image>> call, @NonNull Response<List<Image>> response) {
        if(response.isSuccessful()) {
          List<Image> images = response.body();
          if(state == Base.API_STATE_RANDOM_IMAGE) {
            Glide
              .with(MainActivity.this)
              .load(images.get(0).getUrl())
              .transition(new DrawableTransitionOptions().crossFade())
              .thumbnail(Glide.with(MainActivity.this).load(R.drawable.loading4))
              .into((ImageView) findViewById(R.id.img_banner));
          } else {
            updateRecyclerView(images);
            handleUi(Base.STATE_PROGRESS_DONE);
          }
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

  private void setCall(int state) {
    if(state == Base.API_STATE_RANDOM_IMAGE) {
      call = apiInterface.getRandomImage(Base.API_KEY_DEMO, 1);
    } else {
      call = apiInterface.getImagesList(Base.API_KEY_DEMO, Base.getDate(30), Base.getDate(0));
    }
  }

  private void updateRecyclerView(List<Image> images) {
    //this reverse the list, becouse the list is ascending but we want decsending.
    Collections.reverse(images);

    for(Image image:images){
      imageList.add(new Image(image.getTitle(), image.getDate(), image.getUrl(), image.getMediaType()));
    }
    adapter.notifyDataSetChanged();
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
