package com.valizade.nasaapod.screen.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.valizade.nasaapod.R;
import com.valizade.nasaapod.screen.main.adapter.ImageAdapter;
import com.valizade.nasaapod.utils.MAppCompatActivity;
import com.valizade.nasaapod.Base;
import com.valizade.nasaapod.entities.Image;
import com.valizade.nasaapod.api.old.webservice.OnResponseListener;

import com.valizade.nasaapod.screen.detail.DetailActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MAppCompatActivity implements ImageItemClickListener {

  private static final String IS_NETWORK_AVAILABLE_AND_CONNECTED = "com.valizade.nasaapod.screen.main.is_network_available";

  public static Intent newInstance(Context packageContext) {
    return new Intent(packageContext, MainActivity.class);
  }

  public boolean isNetworkAvailableAndConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
    boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    return isNetworkConnected;
  }





  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  @BindView(R.id.txt_banner_date) TextView txtBannerDate;

  public static final String EXTRA_IMAGE_ITEM = "image_url";
  public static final String EXTRA_IMAGE_TRANSITION_NAME = "image_transition_name";
  private static final String urlNavHeaderBg = "http://pazema.com/wp-content/uploads/2017/04/telescopskystars.jpg";

  private List<Image> imagesList;
  private List<Image> images;
  public boolean isLoadingFinished = true;
  public boolean isFirstLoad = true;
  private boolean isErrorHappend = false;
  private boolean isFirstOnResumed = true;
  private String lastDate;
  private int loadingViewIndex;
  private ImageAdapter adapter;
  private RecyclerView.LayoutManager layoutManager;
  private NavigationView navigationView;
  private DrawerLayout drawer;
  private View navHeader;
  private ImageView imgNavHeaderBg;
  private TextView txtName, txtDescription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    lastDate = Base.getTodayDate();

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    initCollapsingToolbar(getString(R.string.app_name), R.id.collapsing_toolbar, R.id.appbar);

    initRecyclerView();

    initNavMenu();
  }

  public void initNavMenu() {
    drawer = findViewById(R.id.drawer_layout);
    navigationView = findViewById(R.id.nav_view);
    navHeader = navigationView.getHeaderView(0);
    txtName = navHeader.findViewById(R.id.name);
    txtDescription = navHeader.findViewById(R.id.website);
    imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
    loadNavHeader();
    setUpNavigationView();
  }

  private void setLoadingFinished(final int state) {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        switch(state) {
          case Base.STATE_GET_IMAGES_FAILED:
            setErrorState(true);
            showAlertDialog(Base.STATE_CONNECTION_ERROR_LIST, "ERROR", "No found connection! please check your connetion and try agian.",
              "open setting", "cancel");
            break;
          case Base.STATE_GET_IMAGES_SUCCESSFUL:
            removeLastItem();
            break;
        }
        adapter.setLoaded();
        isLoadingFinished = true;
        adapter.notifyDataSetChanged();
      }
    }, Base.HANDLER_DELAY);
  }

  public void getRandomImage() {
    com.valizade.nasaapod.api.old.webservice.Call.getRandomImage(new OnResponseListener() {
      @Override
      public <T> void onResponse(T object) {
        super.onResponse(object);
        setHeaderImage((Image) object);
        getImagesList();
      }

      @Override
      public <T> void onFailure(T object) {
        super.onFailure(object);
        Log.i(Base.APP_TAG, "an error happend");
        Log.e(Base.APP_TAG, object.toString());
        if(object instanceof IOException) {
          showAlertDialog(Base.STATE_CONNECTION_ERROR_HEADER, "ERROR", "No found connection! please check your connetion and try agian.",
            "open setting", "cancel");
        }
      }
    });
  }

  public void getImagesList() {
    addLoadingToRecyclerview();
    com.valizade.nasaapod.api.old.webservice.Call.getImagesList(new OnResponseListener() {
      @Override
      public <T> void onResponse(T object) {
        super.onResponse(object);
        images = (List<Image>) object;
        updateRecyclerView();
        setLoadingFinished(Base.STATE_GET_IMAGES_SUCCESSFUL);
        isFirstLoad = false;
      }

      @Override
      public <T> void onFailure(T object) {
        super.onFailure(object);
        Log.e(Base.APP_TAG, object.toString());
        if(object instanceof IOException) {
          setLoadingFinished(Base.STATE_GET_IMAGES_FAILED);
        }
      }
    }, lastDate);
  }

  private void initRecyclerView() {
    imagesList = new ArrayList<>();
    layoutManager = new GridLayoutManager(this, 2);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new ImageAdapter(this, imagesList, recyclerView, this);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
      @Override
      public void onLoadMore() {
        if(isLoadingFinished && !isFirstLoad && !isErrorHappend) {
          isLoadingFinished = false;
          getImagesList();
        }
      }
    });
    adapter.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        if(!isFirstLoad && isErrorHappend) {
          isLoadingFinished = false;
          setErrorState(false);
          removeLastItem();
          getImagesList();
        }
      }
    });
    recyclerView.setAdapter(adapter);
  }

  public void setErrorState(boolean state) {
    isErrorHappend = state;
    adapter.setErrorState(state);
  }

  public void removeLastItem() {
    imagesList.remove(loadingViewIndex);
    adapter.notifyItemRemoved(loadingViewIndex + 1);
    adapter.notifyDataSetChanged();
  }

  public void addLoadingToRecyclerview() {
    imagesList.add(null);
    adapter.notifyItemInserted(imagesList.size() - 1);
    loadingViewIndex = imagesList.size() -1;
  }

  private void setHeaderImage(final Image image) {
    Glide.with(MainActivity.this)
      .load(image.getUrl())
      .transition(new DrawableTransitionOptions().crossFade())
      .thumbnail(Glide.with(MainActivity.this).load(R.drawable.loading4))
      .listener(new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
          return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
          txtBannerDate.setText(image.getDate());
          txtBannerDate.setVisibility(View.VISIBLE);
          return false;
        }
      })
      .into((ImageView) findViewById(R.id.img_banner));
  }


  private void updateRecyclerView() {
    //this reverse the list, becouse the list is ascending but we want decsending.
    Collections.reverse(images);

    //search images list and just if image.getType() is a "youtube video" or an "image" add to recycler list.
    int limit = 0;
    for(Image image:images){
      if((Base.isAYoutubVideo(image.getUrl()) || image.getMediaType().equals("image")) && limit < 14) {
        imagesList.add(new Image(image.getTitle(), image.getDate(), image.getUrl(), image.getMediaType(),
          image.getCopyright(), image.getExplanation(),false));
        limit++;
      }
    }
    setLastDate();
  }

  private void setLastDate() {
    lastDate = imagesList.get(imagesList.size() - 1).getDate();
    lastDate = Base.minesOneDayOfLastDate(lastDate);
  }

  public void showAlertDialog(final int state, String title, String message, String btnPositive, String btnNegative) {
    isErrorHappend = true;
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(title)
      .setMessage(message)
      .setPositiveButton(btnPositive, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          if(state == Base.STATE_BUG_ERROR_HEADER || state == Base.STATE_BUG_ERROR_LIST) {
            reportBug();
            exitApp();
          } else if(state == Base.STATE_CONNECTION_ERROR_LIST) {
            MainActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            dialog.cancel();
          } else if(state == Base.STATE_CONNECTION_ERROR_HEADER) {
            MainActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            isFirstOnResumed = true;
            dialog.cancel();
          }
        }
      })
      .setNegativeButton(btnNegative, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          if(state == Base.STATE_BUG_ERROR_HEADER || state == Base.STATE_CONNECTION_ERROR_HEADER) {
            exitApp();
          } else if(state == Base.STATE_CONNECTION_ERROR_LIST || state == Base.STATE_BUG_ERROR_LIST) {
            dialog.cancel();
          }
        }
      })
      .show();
  }

  public void reportBug() {

  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.i(Base.APP_TAG, "onResume");
    if(isFirstOnResumed) {
      Log.i(Base.APP_TAG, "ifFirstLoad!");
      getRandomImage();
      isFirstOnResumed = false;
    }
  }

  public void exitApp() {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    System.exit(1);
  }

  @Override
  public void onImageClick(int pos, Image image, ImageView shareImageView) {
    Intent intent = new Intent(this, DetailActivity.class);
    Log.i("mv_test_nasa", "title of image is: " + image.getTitle());
    intent.putExtra(EXTRA_IMAGE_ITEM, image);
    intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(shareImageView));

    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, shareImageView,
      ViewCompat.getTransitionName(shareImageView));

    startActivity(intent, options.toBundle());
  }


  public void loadNavHeader() {
    // name, desctiption
    txtName.setText("Nasa APOD");
    txtDescription.setText("Astronomy Picture Of Day");

    // loading header background image
    Glide.with(this).load(urlNavHeaderBg)
      .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
      .into(imgNavHeaderBg);
  }

  private void setUpNavigationView() {
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
          case R.id.nav_astronomer:
            drawer.closeDrawers();
            return true;
          case R.id.nav_setting:
            drawer.closeDrawers();
            return true;
          case R.id.nav_about:
            drawer.closeDrawers();
            return true;
          case R.id.nav_share:
            drawer.closeDrawers();
            return true;
          case R.id.nav_contact:
            drawer.closeDrawers();
            return true;
        }
        return true;
      }
    });

    ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.openDrawer, R.string.closeDrawer) {
      @Override
      public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
      }
      @Override
      public void onDrawerOpened(View drawerView) {
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

