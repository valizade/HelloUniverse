package com.valizade.hellouniverse.screen.imagelist.di;

import com.valizade.hellouniverse.libs.base.ImageLoader;
import com.valizade.hellouniverse.libs.di.LibraryModule;
import com.valizade.hellouniverse.screen.imagelist.ImageListContract;
import com.valizade.hellouniverse.screen.imagelist.ui.ImageListActivity;
import com.valizade.hellouniverse.screen.imagelist.ui.ImageListFragment;
import com.valizade.hellouniverse.screen.imagelist.ui.adapter.ImageListAdapter;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {ImageListModule.class, LibraryModule.class})
public interface ImageListComponent {

  void inject(ImageListActivity activity);
}
