package com.valizade.hellouniverse.screen.imagelist.ui;

import androidx.fragment.app.Fragment;

public class ImageListFragment extends Fragment {

  /*@BindView(R.id.recycler_view)
  RecyclerView mRecyclerView;
  @BindView(R.id.progressBar)
  ProgressBar mProgressBar;
  *//*@BindView(R.id.img_banner)
  ImageView mHeaderImageView;*//*

  private ImageListContract.Presenter mPresenter;
  private ImageListAdapter mAdapter;
  private ImageLoader mImageLoader;

  //TODO when all is done and run the app successfully then delete this constructor and see what will happen?
  public ImageListFragment() {
    // Requires empty public constructor
    Log.d("TestTag", "in the ImageListFragment");
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.lmagelist_rv, container, false);
    ButterKnife.bind(this, root);

    setupInjection();
    Log.d("TestTag", "in the onCreateView");

    mProgressBar.setVisibility(View.VISIBLE);

    mRecyclerView.setVisibility(View.GONE);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

    //TODO getout this line from comment
    //mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    showProgressbar(true);
    mPresenter.getImageList();
    mPresenter.getHeaderImage();

    mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
      @Override
      public void onLoadMore() {
        showProgressbar(true);
        mPresenter.loadMoreImage();
      }
    });

    return root;
  }

  private void setupInjection() {
    *//*Base application = new Base();
    ImageListComponent component = application.getImageListComponent(getActivity(), this, this);
    mPresenter = component.getPresenter();
    mAdapter = component.getImageListAdapter();
    mImageLoader = component.getImageLoader();*//*
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

  *//*@Override
  public void setPresenter(@NonNull ImageListContract.Presenter presenter) {
    mPresenter = checkNotNull(presenter);
  }*//*

  @Override
  public void showUi(boolean show) {

  }

  @Override
  public void showErrorMessage(String errorMessage) {

  }

  @Override
  public void showList(boolean show) {
    mRecyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
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
  public void onLoadListError(String errorMessage) {
    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
  }

  @Override
  public void setHeaderContent(Image image) {
//    mImageLoader.load(mHeaderImageView, image.getUrl());
    Toast.makeText(getActivity(), "imagebanner is loaded", Toast.LENGTH_LONG).show();
  }

  @Override
  public void onLoadHeaderError(String errorMessage) {
    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
  }

  *//*@Override
  public boolean isActive() {
    return isAdded();
  }*//*

  @Override
  public void onClick(Image image, ImageView shareImageView) {
    Toast.makeText(getActivity(), "Navigate to the DetailActivity", Toast.LENGTH_LONG).show();
  }*/
}
