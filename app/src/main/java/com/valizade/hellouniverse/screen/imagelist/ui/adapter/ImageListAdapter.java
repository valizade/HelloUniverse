package com.valizade.hellouniverse.screen.imagelist.ui.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.valizade.hellouniverse.R;
import com.valizade.hellouniverse.entities.Image;
import com.valizade.hellouniverse.libs.base.ImageLoader;
import com.valizade.hellouniverse.screen.imagelist.ui.OnImageListLoadListener;
import com.valizade.hellouniverse.screen.imagelist.ui.OnItemClickListener;
import com.valizade.hellouniverse.screen.imagelist.ui.adapter.ImageListAdapter.ImageListViewHolder;
import java.util.List;
import javax.inject.Named;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListViewHolder> {

  private List<Image> mImageList;
  private ImageLoader mImageLoader;
  private OnItemClickListener mClickListener;
  private OnImageListLoadListener mImageListLoadListener;

  private boolean firstLoadedItem = true;

  public ImageListAdapter(List<Image> imageList, @Named("listImageLoader") ImageLoader imageLoader,
      OnItemClickListener clickListener, OnImageListLoadListener imageListLoadListener) {
    mImageList = imageList;
    mImageLoader = imageLoader;
    mClickListener = clickListener;
    mImageListLoadListener = imageListLoadListener;
  }

  @NonNull
  @Override
  public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.image_item, parent, false);
    return new ImageListViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ImageListViewHolder holder, int position) {
    Image image = mImageList.get(position);
    holder.bind(image);
  }

  @Override
  public int getItemCount() {
    return mImageList.size();
  }

  public void setImageList(List<Image> images) {
    mImageList.addAll(images);
    notifyDataSetChanged();
  }

  public class ImageListViewHolder extends RecyclerView.ViewHolder {

    //TODO Implement algorithm for show/hide play view on the images that are video
    @BindView(R.id.imageitem_img_play)
    ImageView mPlayImageView;

    @BindView(R.id.imageitem_img_thumbnail)
    ImageView mThumbnailImageView;
    @BindView(R.id.imageitem_txt_title)
    TextView mTitleTextView;
    @BindView(R.id.imageitem_txt_date)
    TextView mDateTextView;

    public ImageListViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void bind(Image image) {
      mTitleTextView.setText(image.getTitle());
      mDateTextView.setText(image.getDate());

      //TODO I think I should move this line to the view (ImageListFragment)
      ViewCompat.setTransitionName(mThumbnailImageView, image.getTitle());

      setOnImageClickListener(image);
      setOnImageLoadListener(image);

      /*
       * Notice that call to load on a glide image loader must be after the listener is set (if we have a listener)
       * */
      mImageLoader.load(mThumbnailImageView, image.getUrl());
    }

    private void setOnImageClickListener(Image image) {
      mThumbnailImageView.setOnClickListener(v -> {
        if (getAdapterPosition() >= 0) {
          if (image.isLoaded()) {
            mClickListener.onClick(image, mThumbnailImageView);
          }
        }
      });
    }

    private void setOnImageLoadListener(Image image) {
      RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target,
            boolean isFirstResource) {
          return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
            DataSource dataSource, boolean isFirstResource) {
          if (firstLoadedItem) {
            firstLoadedItem = false;
            mImageListLoadListener.onImageLoad(true);
          }
          if (getAdapterPosition() >= 0) {
            if (!image.isLoaded()) {
              image.setLoaded(true);
            }
          }
          return false;
        }
      };
      mImageLoader.setOnFinishLoadingImageListener(requestListener);
    }
  }
}
