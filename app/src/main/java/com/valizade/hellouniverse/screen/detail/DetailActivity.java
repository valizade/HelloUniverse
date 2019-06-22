package com.valizade.hellouniverse.screen.detail;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.valizade.hellouniverse.R;
import com.valizade.hellouniverse.entities.Image;
import com.valizade.hellouniverse.screen.fullsizeimage.FullSizeImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DetailActivity extends AppCompatActivity {

  private static final String EXTRA_IMAGE_ITEM = "com.valizade.hellouniverse.screen.detail.extraImageItem";
  private static final String EXTRA_IMAGE_TRANSITION_NAME = "com.valizade.hellouniverse.screen.detail.extraImageTransitionName";
  private static final String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

  @BindView(R.id.imagedetail_toolbar)
  Toolbar mToolbar;
  @BindView(R.id.imagedetail_img_banner)
  ImageView mBannerImageView;
  @BindView(R.id.imagedetail_txt_copywrite)
  TextView mCopyrightTextView;
  @BindView(R.id.imagedetail_txt_description)
  TextView mDescriptionTextView;

  boolean isImageFitToScreen;
  private String mImageUrl;
  private String mImageTitle;
  private String mImageDate;
  private DownloadManager downloadManager;
  private long mDownloadReferenceId;
  private BroadcastReceiver onComplete = new BroadcastReceiver() {
    public void onReceive(Context ctxt, Intent intent) {
      sendNotification();
    }
  };

  public static Intent newInstance(Context packageName, Image image, ImageView shareImageView) {
    Intent intent = new Intent(packageName, DetailActivity.class);
    intent.putExtra(EXTRA_IMAGE_ITEM, image);
    intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(shareImageView));
    return intent;
  }

  private void sendNotification() {
    NotificationManager notificationManager = (NotificationManager) getSystemService(
        Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
          "My Notifications", NotificationManager.IMPORTANCE_HIGH);
      notificationChannel.setDescription("Channel description");
      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(Color.BLUE);
      notificationManager.createNotificationChannel(notificationChannel);
    }

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
        DetailActivity.this,
        NOTIFICATION_CHANNEL_ID);
    notificationBuilder
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(R.drawable.ic_action_done)
        .setTicker("Download Completed!")
        .setContentTitle("Download Completed!")
        .setContentText("Download The " + mImageTitle + ".png")
//          .setContentIntent(pi)
        .setContentInfo("Download Completed!");
    notificationManager.notify(1, notificationBuilder.build());
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.imagedetail_act_copy);
    ButterKnife.bind(this);
    supportPostponeEnterTransition();

    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

    setSupportActionBar(mToolbar);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);

    Bundle extras = getIntent().getExtras();
    Image imageItem = extras.getParcelable(EXTRA_IMAGE_ITEM);

    // TODO: use hd url and progressbar as placeholder
//    mImageUrl = imageItem.getHdurl() != null ? imageItem.getHdurl() : imageItem.getUrl();
    mImageUrl = imageItem.getUrl();
    mImageTitle = imageItem.getTitle();
    mImageDate = imageItem.getDate();

    ab.setTitle(imageItem.getTitle());
    ab.setSubtitle(imageItem.getDate());
    mCopyrightTextView.setText(
        imageItem.getCopyright() != null ? "Copyright: " + imageItem.getCopyright()
            : "Copyright: Public Domain");
    mDescriptionTextView.setText(Html.fromHtml(imageItem.getExplanation()));

    String imageUrl = imageItem.getUrl();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      String imageTransitionName = extras.getString(EXTRA_IMAGE_TRANSITION_NAME);
      mBannerImageView.setTransitionName(imageTransitionName);
    }

    Glide.with(this)
        .load(imageUrl)
        .listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model,
              Target<Drawable> target, boolean isFirstResource) {
            supportStartPostponedEnterTransition();
            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
              DataSource dataSource, boolean isFirstResource) {
            supportStartPostponedEnterTransition();
            return false;
          }
        })
        .into(mBannerImageView);
  }

  @OnClick(R.id.imagedetail_img_banner)
  public void onNasaApodClickListener() {
    // TODO: use hd url and progressbar as placeholder
    Intent intent = FullSizeImage.newInstance(this, mImageUrl);
    startActivity(intent);
  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(onComplete);
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.imagedetail_act, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
        // TODO: override title toolbar click for show the full title
      case android.R.id.title:
        Toast.makeText(this, "hello!", Toast.LENGTH_SHORT).show();
        return true;
      case R.id.imagedetail_action_share:
        shareImageUrl();
        return true;
      case R.id.imagedetail_action_set_as:
        return true;
      case R.id.imagedetail_action_download:
        if (isStoragePermissionGranted()) {
          downloadImage();
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public boolean isStoragePermissionGranted() {
    if (Build.VERSION.SDK_INT >= 23) {
      if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED) {
        return true;
      } else {
        ActivityCompat
            .requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        return false;
      }
    } else { //permission is automatically granted on sdk<23 upon installation
      return true;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      downloadImage();
    } else {
      Toast.makeText(this, "We need permission to save image on your phone!", Toast.LENGTH_LONG)
          .show();
    }
  }

  private void downloadImage() {
    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mImageUrl));
    request.setAllowedNetworkTypes(
        DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
    request.setAllowedOverRoaming(false);
    request.setTitle("Downloading " + mImageTitle + ".png");
    request.setDescription("Downloading " + mImageTitle + ".png");
    request.setVisibleInDownloadsUi(true);
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
        "/Nasa APOD/" + "/" + mImageTitle + mImageDate + ".png");
    mDownloadReferenceId = downloadManager.enqueue(request);
  }

  private void shareImageUrl() {
    Intent share = new Intent(android.content.Intent.ACTION_SEND);
    share.setType("text/plain");
    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    share.putExtra(Intent.EXTRA_SUBJECT, "Nasa APOD of " + mImageDate);
    share.putExtra(Intent.EXTRA_TEXT, mImageUrl);
    startActivity(Intent.createChooser(share, "Share Image"));
  }
}

