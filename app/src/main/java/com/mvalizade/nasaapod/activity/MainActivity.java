package com.mvalizade.nasaapod.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mvalizade.nasaapod.R;
import com.mvalizade.nasaapod.adapter.ImageAdapter;
import com.mvalizade.nasaapod.framework.activity.MAppCompatActivity;
import com.mvalizade.nasaapod.framework.application.Base;
import com.mvalizade.nasaapod.framework.transform.CircleTransform;
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




  private NavigationView navigationView;
  private DrawerLayout drawer;
  private View navHeader;
  private ImageView imgNavHeaderBg, imgProfile;
  private TextView txtName, txtWebsite;

  // urls to load navigation header background image
  // and profile image
  private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
  private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

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
    //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);


    drawer = findViewById(R.id.drawer_layout);
    navigationView = findViewById(R.id.nav_view);

    // Navigation view header
    navHeader = navigationView.getHeaderView(0);
    txtName = navHeader.findViewById(R.id.name);
    txtWebsite = navHeader.findViewById(R.id.website);
    imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
    imgProfile = navHeader.findViewById(R.id.img_profile);

    // load nav menu header data
    loadNavHeader();

    // initializing navigation menu
    setUpNavigationView();
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
            if(images.get(0).getMediaType().equals("image")) {
              setHeaderImage(images);
              getImage(Base.API_STATE_LIST_IMAGE);
            } else {
              getImage(Base.API_STATE_RANDOM_IMAGE);
            }
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

  private void setHeaderImage(List<Image> images) {
    Glide
      .with(MainActivity.this)
      .load(images.get(0).getUrl())
      .transition(new DrawableTransitionOptions().crossFade())
      .thumbnail(Glide.with(MainActivity.this).load(R.drawable.loading4))
      .into((ImageView) findViewById(R.id.img_banner));
  }

  private void setCall(int state) {
    if(state == Base.API_STATE_RANDOM_IMAGE) {
      call = apiInterface.getRandomImage(Base.API_KEY, 1);
    } else {
      call = apiInterface.getImagesList(Base.API_KEY_DEMO, Base.getDate(15), Base.getDate(0));
    }
  }

  private void updateRecyclerView(List<Image> images) {
    //this reverse the list, becouse the list is ascending but we want decsending.
    Collections.reverse(images);

    //search images list and just if image.getType() is a "youtube video" or an "image" add to recycler list.
    for(Image image:images){
      if(Base.isAYoutubVideo(image.getUrl()) || image.getMediaType().equals("image")) {
        imageList.add(new Image(image.getTitle(), image.getDate(), image.getUrl(), image.getMediaType()));
      }
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

  /***
   * Load navigation menu header information
   * like background image, profile image
   * name, website, notifications action view (dot)
   */
  private void loadNavHeader() {
    // name, website
    txtName.setText("Ravi Tamada");
    txtWebsite.setText("www.androidhive.info");

    // loading header background image
    Glide.with(this).load(urlNavHeaderBg)
      .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
      .into(imgNavHeaderBg);

    // Loading profile image
    Glide.with(this).load(urlProfileImg)
      .transition(new DrawableTransitionOptions().crossFade())
      .thumbnail(0.5f)
      .apply(RequestOptions.bitmapTransform(new CircleTransform(this)))
      .into(imgProfile);
  }

  private void setUpNavigationView() {
    //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

      // This method will trigger on item Click of navigation menu
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId()) {
          //Replacing the main content with ContentFragment Which is our Inbox View;
          case R.id.nav_home:
            // launch new intent instead of loading fragment
            //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            drawer.closeDrawers();
            return true;
          case R.id.nav_photos:
            // launch new intent instead of loading fragment
            //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            drawer.closeDrawers();
            return true;
          case R.id.nav_movies:
            // launch new intent instead of loading fragment
            //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            drawer.closeDrawers();
            return true;
          case R.id.nav_notifications:
            // launch new intent instead of loading fragment
            //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            drawer.closeDrawers();
            return true;
          case R.id.nav_settings:
            // launch new intent instead of loading fragment
            //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            drawer.closeDrawers();
            return true;
          case R.id.nav_about_us:
            // launch new intent instead of loading fragment
            //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            drawer.closeDrawers();
            return true;
          case R.id.nav_privacy_policy:
            // launch new intent instead of loading fragment
            //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
            drawer.closeDrawers();
            return true;
        }
        return true;
      }
    });


    ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.openDrawer, R.string.closeDrawer) {

      @Override
      public void onDrawerClosed(View drawerView) {
        // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
        super.onDrawerClosed(drawerView);
      }

      @Override
      public void onDrawerOpened(View drawerView) {
        // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
        super.onDrawerOpened(drawerView);
      }
    };

    //calling sync state is necessary or else your hamburger icon wont show up
    actionBarDrawerToggle.syncState();
  }

  @Override
  public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawers();
      return;
    }
    super.onBackPressed();
  }

}

