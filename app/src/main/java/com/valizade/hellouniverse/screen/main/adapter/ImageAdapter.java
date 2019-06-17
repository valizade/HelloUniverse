package com.valizade.hellouniverse.screen.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.valizade.hellouniverse.R;
import com.valizade.hellouniverse.Base;
import com.valizade.hellouniverse.screen.main.ImageItemClickListener;
import com.valizade.hellouniverse.screen.main.OnLoadMoreListener;
import com.valizade.hellouniverse.screen.main.OnRefreshListener;
import com.valizade.hellouniverse.entities.Image;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private Context context;
  public List<Image> images;
  private Image image;
  private ImageItemClickListener imageItemClickListener;

  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  private OnLoadMoreListener onLoadMoreListener;
  private OnRefreshListener onRefreshListener;
  private boolean isLoading;
  private boolean isErrorHappend;
  private int visibleThreshold = 6;
  private int pastVisiblesItems, totalItemCount;

  public ImageAdapter(Context context, List<Image> images, RecyclerView recyclerView, ImageItemClickListener imageItemClickListener) {
    this.context = context;
    this.images = images;
    this.imageItemClickListener = imageItemClickListener;
    final LinearLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        totalItemCount = layoutManager.getItemCount();
        pastVisiblesItems = layoutManager.findLastVisibleItemPosition();
        if (!isLoading && totalItemCount <= (pastVisiblesItems + visibleThreshold)) {
          if (onLoadMoreListener != null) {
            onLoadMoreListener.onLoadMore();
          }
          isLoading = true;
        }
      }
    });
  }

  public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
    this.onLoadMoreListener = onLoadMoreListener;
  }

  public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
    this.onRefreshListener = onRefreshListener;
  }

  @Override
  public int getItemViewType(int position) {
    return images.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  public class ImageViewHolder extends RecyclerView.ViewHolder {

    public TextView txtTitle, txtDate;
    public ImageView imgThumbnail, imgPlay;

    public ImageViewHolder(View itemView) {
      super(itemView);
      txtTitle = itemView.findViewById(R.id.txt_splash_title);
      txtDate = itemView.findViewById(R.id.txt_date);
      imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
      imgPlay = itemView.findViewById(R.id.img_play);
    }
  }

  public class LoadingViewHolder extends RecyclerView.ViewHolder {

    public ProgressBar progressBar;
    public ImageView refresh;

    public LoadingViewHolder(View itemView) {
      super(itemView);
      progressBar = itemView.findViewById(R.id.progressBar1);
      refresh = itemView.findViewById(R.id.img_refresh);
    }
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView;
    if (viewType == VIEW_TYPE_ITEM) {
      itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter, parent, false);
      return new ImageAdapter.ImageViewHolder(itemView);
    } else if (viewType == VIEW_TYPE_LOADING) {
      itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
      return new ImageAdapter.LoadingViewHolder(itemView);
    }
    return null;
  }

  @Override
  public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ImageAdapter.ImageViewHolder) {
      image = images.get(position);
      final ImageAdapter.ImageViewHolder imageViewHolder = (ImageAdapter.ImageViewHolder) holder;
      imageViewHolder.txtTitle.setText(image.getTitle());
      imageViewHolder.txtDate.setText(image.getDate());
      Glide
        .with(context)
        .load(getImageThumbnail(imageViewHolder))
        .listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            if(imageViewHolder.getAdapterPosition() >= 0) {
              if (!images.get(imageViewHolder.getAdapterPosition()).getLoaded()) {
                images.get(imageViewHolder.getAdapterPosition()).setLoaded(true);
              }
            }
            return false;
          }
        })
        .into(imageViewHolder.imgThumbnail);

      ViewCompat.setTransitionName(imageViewHolder.imgThumbnail, image.getTitle());
      imageViewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(imageViewHolder.getAdapterPosition() >= 0) {
            if(images.get(imageViewHolder.getAdapterPosition()).getLoaded()) {
              imageItemClickListener.onImageClick(imageViewHolder.getAdapterPosition(), images.get(imageViewHolder.getAdapterPosition()), imageViewHolder.imgThumbnail);
            }
          }
        }
      });

    } else if (holder instanceof ImageAdapter.LoadingViewHolder) {
      ImageAdapter.LoadingViewHolder loadingViewHolder = (ImageAdapter.LoadingViewHolder) holder;
      if(!isErrorHappend) {
        loadingViewHolder.refresh.setVisibility(View.GONE);
        loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
        loadingViewHolder.progressBar.setIndeterminate(true);
      } else {
        loadingViewHolder.refresh.setVisibility(View.VISIBLE);
        loadingViewHolder.progressBar.setVisibility(View.GONE);
      }
      loadingViewHolder.refresh.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onRefreshListener.onRefresh();
        }
      });
    }
  }

  //get image and video thumbnail and if its a video, add a image (play picture) to thumbnail.
  public String getImageThumbnail(ImageAdapter.ImageViewHolder imageViewHolder) {
    String url = image.getUrl();
    switch (image.getMediaType()) {
      case "video" :
        imageViewHolder.imgPlay.setVisibility(View.VISIBLE);
        return ("http://img.youtube.com/vi/" + Base.extractYoutubeId(url) + "/0.jpg");
      case "image" :
        imageViewHolder.imgPlay.setVisibility(View.GONE);
        return url;
      default:
        return "";
    }
  }

  @Override
  public int getItemCount() {
    return images == null ? 0 : images.size();
  }

  public void setLoaded() {
    isLoading = false;
  }

  public void setErrorState(boolean state) {
    isErrorHappend = state;
  }
}