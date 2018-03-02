package com.mvalizade.nasaapod.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mvalizade.nasaapod.R;
import com.mvalizade.nasaapod.model.Image;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

  private Context context;
  public List<Image> images;

  public ImageAdapter(Context context, List<Image> images) {
    this.context = context;
    this.images = images;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView txtTitle, txtDate;
    public ImageView imgThumbnail;

    public MyViewHolder(View itemView) {
      super(itemView);
      txtTitle = itemView.findViewById(R.id.txt_title);
      txtDate = itemView.findViewById(R.id.txt_date);
      imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
    }
  }

  @Override
  public ImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.adapter, parent, false);
    return new MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(ImageAdapter.MyViewHolder holder, int position) {
    Image image = images.get(position);
    holder.txtTitle.setText(image.getTitle());
    holder.txtDate.setText(image.getDate());
    Glide.with(context)
      .load(image.getUrl())
      .into(holder.imgThumbnail);
  }

  @Override
  public int getItemCount() {
    return images.size();
  }
}
