package com.valizade.hellouniverse.screen.imagelist.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.valizade.hellouniverse.Base;
import com.valizade.hellouniverse.R;
import com.valizade.hellouniverse.entities.Image;
import com.valizade.hellouniverse.libs.base.ImageLoader;
import com.valizade.hellouniverse.screen.imagelist.ImageListContract;
import com.valizade.hellouniverse.screen.imagelist.di.ImageListComponent;
import com.valizade.hellouniverse.screen.imagelist.ui.adapter.ImageListAdapter;
import com.valizade.hellouniverse.utils.EndlessRecyclerOnScrollListener;
import com.valizade.hellouniverse.utils.RecyclerViewUtils;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class ImageListActivity extends AppCompatActivity implements ImageListContract.View,
    OnItemClickListener, OnImageListLoadListener {

  @BindView(R.id.imagelist_toolbar)
  Toolbar mToolbar;
  @BindView(R.id.imagelist_collapsing_toolbar)
  CollapsingToolbarLayout mCollapsingToolbarLayout;
  @BindView(R.id.imagelist_appbar)
  AppBarLayout mAppBarLayout;
  @BindView(R.id.imagelist_img_banner)
  ImageView mHeaderImageView;
  @BindView(R.id.imagelist_drawer_layout)
  DrawerLayout mDrawerLayout;
  @BindView(R.id.imagelist_nav_view)
  NavigationView mNavigationView;
  @BindView(R.id.imagelist_rv)
  RecyclerView mRecyclerView;
  @BindView(R.id.imagelist_rv_progressBar)
  ProgressBar mProgressBar;
  @BindView(R.id.imagelist_txt_error_getResponseFailed)
  TextView mErrorScreen;
  @BindView(R.id.imagelist_inc)
  CoordinatorLayout mUiContent;
  @BindView(R.id.imagelist_progressbar)
  ProgressBar mMainProgressBar;

  @Inject
  ImageListContract.Presenter mPresenter;
  @Inject
  ImageListAdapter mAdapter;
  @Inject
  @Named("headerImageLoader")
  ImageLoader mHeaderImageLoader;

  public static Intent newInstance(Context packageName) {
    Intent intent = new Intent(packageName, ImageListActivity.class);
    return intent;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.imagelist_act);
    ButterKnife.bind(this);

    setupInjection();
    setupToolbar();
    setupRecyclerView();

    // Set up the navigation drawer.
    mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
    if (mNavigationView != null) {
      setupDrawerContent();
    }

    mPresenter.getHeaderImage();
    mPresenter.getImageList();
  }

  private void setupRecyclerView() {
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mRecyclerView.addItemDecoration(
        new RecyclerViewUtils(2, RecyclerViewUtils.dpToPx(4, getResources()), true));
    mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
      @Override
      public void onLoadMore() {
        mPresenter.loadMoreImage();
      }
    });
  }

  private void setupToolbar() {
    // Set up the toolbar.
    setSupportActionBar(mToolbar);
    ActionBar ab = getSupportActionBar();
    ab.setHomeAsUpIndicator(R.drawable.ic_menu);
    ab.setDisplayHomeAsUpEnabled(true);

    // Set up the collapsing toolbar.
    mCollapsingToolbarLayout.setTitle(" ");
    mAppBarLayout.setExpanded(true);
    mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = false;
      int scrollRange = -1;

      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
          isShow = true;
        } else if (isShow) {
          mCollapsingToolbarLayout.setTitle(" ");
          isShow = false;
        }
      }
    });
  }

  private void setupInjection() {
    Base application = new Base();
    ImageListComponent component = application.getImageListComponent(this, this, this, this);
    component.inject(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // Open the navigation drawer when the home icon is selected from the toolbar.
        mDrawerLayout.openDrawer(GravityCompat.START);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupDrawerContent() {
    mNavigationView.setNavigationItemSelectedListener(menuItem -> {
      switch (menuItem.getItemId()) {
        case R.id.nav_astronomer:
          break;
        case R.id.nav_setting:
          break;
        case R.id.nav_about:
          break;
        case R.id.nav_share:
          break;
        case R.id.nav_contact:
          break;
        default:
          break;
      }
      // Close the navigation drawer when an item is selected.
      menuItem.setChecked(true);
      mDrawerLayout.closeDrawers();
      return true;
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.onResume();
  }

  @Override
  public void onPause() {
    mPresenter.onPause();
    super.onPause();
  }

  @Override
  public void onDestroy() {
    mPresenter.onDestroy();
    super.onDestroy();
  }

  @Override
  public void showUi(boolean show) {
    mUiContent.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showMainProgressbar(boolean show) {
    mMainProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showProgressbar(boolean show) {
    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setListContent(List<Image> images) {
    mAdapter.setImageList(images);
  }

  @Override
  public void setHeaderContent(Image image) {
    // I need do this because i need expose ui when header image is loaded.
    RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
      @Override
      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
          boolean isFirstResource) {
        Log.d("testTag0", "imageHeader load is failed!");
        return false;
      }

      @Override
      public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
          DataSource dataSource, boolean isFirstResource) {
        Log.d("testTag0", "imageHeader is ready!");
        return false;
      }
    };
    mHeaderImageLoader.setOnFinishLoadingImageListener(requestListener);

    /*
     * Notice that call to load on a glide image loader must be after the listener is set (if we have a listener)
     * */
    mHeaderImageLoader.load(mHeaderImageView, image.getUrl());
  }

  @Override
  public void showErrorScreen(boolean show) {
    mErrorScreen.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showErrorMessage(String errorMessage) {
    Snackbar.make(mUiContent, "Error Load! Please check your connection and then swip up to referesh.", Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void onClick(Image image, ImageView shareImageView) {
    Toast.makeText(this, "Navigate to the DetailActivity", Toast.LENGTH_LONG).show();
  }

  @Override
  public void onImageLoad(boolean isSuccess) {
    Log.d("testTag0", "onImageLoad");
  }
}
