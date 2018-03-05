package com.mvalizade.nasaapod.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
  private Toolbar toolbar;
  private FloatingActionButton fab;

  // urls to load navigation header background image
  // and profile image
  private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
  private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

  // index to identify current nav menu item
  public static int navItemIndex = 0;

  // tags used to attach the fragments
  private static final String TAG_HOME = "home";
  private static final String TAG_PHOTOS = "photos";
  private static final String TAG_MOVIES = "movies";
  private static final String TAG_NOTIFICATIONS = "notifications";
  private static final String TAG_SETTINGS = "settings";
  public static String CURRENT_TAG = TAG_HOME;

  // toolbar titles respected to selected nav menu item
  private String[] activityTitles;

  // flag to load home fragment when user presses back key
  private boolean shouldLoadHomeFragOnBackPress = true;
  private Handler mHandler;



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

    //getImage(Base.API_STATE_LIST_IMAGE);

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
      call = apiInterface.getImagesList(Base.API_KEY, Base.getDate(30), Base.getDate(0));
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
      .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CircleTransform(this)))
      .into(imgProfile);

    // showing dot next to notifications label
    navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
  }

  /***
   * Returns respected fragment that user
   * selected from navigation menu
   */
  private void loadHomeFragment() {
    // selecting appropriate nav menu item
    selectNavMenu();

    // set toolbar title
    setToolbarTitle();

    // if user select the current navigation menu again, don't do anything
    // just close the navigation drawer
    if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
      drawer.closeDrawers();

      // show or hide the fab button
      toggleFab();
      return;
    }

    // Sometimes, when fragment has huge data, screen seems hanging
    // when switching between navigation menus
    // So using runnable, the fragment is loaded with cross fade effect
    // This effect can be seen in GMail app
    /*Runnable mPendingRunnable = new Runnable() {
      @Override
      public void run() {
        // update the main content by replacing fragments
        Fragment fragment = getHomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
          android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
      }
    };*/

    // If mPendingRunnable is not null, then add to the message queue
    /*if (mPendingRunnable != null) {
      mHandler.post(mPendingRunnable);
    }*/

    // show or hide the fab button
    toggleFab();

    //Closing drawer on item click
    drawer.closeDrawers();

    // refresh toolbar menu
    invalidateOptionsMenu();
  }


  private void setToolbarTitle() {
    getSupportActionBar().setTitle(activityTitles[navItemIndex]);
  }

  private void selectNavMenu() {
    navigationView.getMenu().getItem(navItemIndex).setChecked(true);
  }

  private void setUpNavigationView() {
    //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

      // This method will trigger on item Click of navigation menu
      @Override
      public boolean onNavigationItemSelected(MenuItem menuItem) {

        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId()) {
          //Replacing the main content with ContentFragment Which is our Inbox View;
          case R.id.nav_home:
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            break;
          case R.id.nav_photos:
            navItemIndex = 1;
            CURRENT_TAG = TAG_PHOTOS;
            break;
          case R.id.nav_movies:
            navItemIndex = 2;
            CURRENT_TAG = TAG_MOVIES;
            break;
          case R.id.nav_notifications:
            navItemIndex = 3;
            CURRENT_TAG = TAG_NOTIFICATIONS;
            break;
          case R.id.nav_settings:
            navItemIndex = 4;
            CURRENT_TAG = TAG_SETTINGS;
            break;
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
          default:
            navItemIndex = 0;
        }

        //Checking if the item is in checked state or not, if not make it in checked state
        if (menuItem.isChecked()) {
          menuItem.setChecked(false);
        } else {
          menuItem.setChecked(true);
        }
        menuItem.setChecked(true);

        loadHomeFragment();

        return true;
      }
    });


    ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

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

    //Setting the actionbarToggle to drawer layout
    drawer.setDrawerListener(actionBarDrawerToggle);

    //calling sync state is necessary or else your hamburger icon wont show up
    actionBarDrawerToggle.syncState();
  }

  @Override
  public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawers();
      return;
    }

    // This code loads home fragment when back key is pressed
    // when user is in other fragment than home
    if (shouldLoadHomeFragOnBackPress) {
      // checking if user is on other navigation menu
      // rather than home
      if (navItemIndex != 0) {
        navItemIndex = 0;
        CURRENT_TAG = TAG_HOME;
        loadHomeFragment();
        return;
      }
    }

    super.onBackPressed();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.

    // show menu only when home fragment is selected
    if (navItemIndex == 0) {
      getMenuInflater().inflate(R.menu.mian, menu);
    }

    // when fragment is notifications, load the menu created for notifications
    if (navItemIndex == 3) {
      getMenuInflater().inflate(R.menu.notification, menu);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_logout) {
      Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
      return true;
    }

    // user is in notifications fragment
    // and selected 'Mark all as Read'
    if (id == R.id.action_mark_all_read) {
      Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
    }

    // user is in notifications fragment
    // and selected 'Clear All'
    if (id == R.id.action_clear_notifications) {
      Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
    }

    return super.onOptionsItemSelected(item);
  }

  // show or hide the fab
  private void toggleFab() {
    if (navItemIndex == 0)
      fab.show();
    else
      fab.hide();
  }
}
