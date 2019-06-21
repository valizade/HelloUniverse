package com.valizade.hellouniverse.screen.detail;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.valizade.hellouniverse.R;

public class DetailActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.imagedetail_act);
    supportPostponeEnterTransition();

    /*Bundle extras = getIntent().getExtras();
    Image imageItem = extras.getParcelable(MainActivity.EXTRA_IMAGE_ITEM);

    ImageView imageView = findViewById(R.id.detail_image_view);
    TextView txtTitle = findViewById(R.id.txt_title);
    TextView txtCopyWrite = findViewById(R.id.txt_copywrite);
    TextView txtDate = findViewById(R.id.txt_date);
    TextView txtDesc = findViewById(R.id.txt_desc);

    if(imageItem.getCopyright() != null) {
      txtCopyWrite.setText(imageItem.getCopyright());
    } else {
      txtCopyWrite.setVisibility(View.GONE);
    }
    txtTitle.setText(imageItem.getTitle());
    txtDate.setText(imageItem.getDate());
    txtDesc.setText(imageItem.getExplanation());

    String imageUrl = imageItem.getUrl();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      String imageTransitionName = extras.getString(MainActivity.EXTRA_IMAGE_TRANSITION_NAME);
      imageView.setTransitionName(imageTransitionName);
    }

    Glide.with(this)
      .load(imageUrl)
      .listener(new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
          supportStartPostponedEnterTransition();
          return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
          supportStartPostponedEnterTransition();
          return false;
        }
      })
      .into(imageView);*/
  }
}
