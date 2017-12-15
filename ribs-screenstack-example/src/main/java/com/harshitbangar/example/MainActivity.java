package com.harshitbangar.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import com.harshitbangar.example.root.RootBuilder;
import com.harshitbangar.example.root.RootInteractor;
import com.harshitbangar.example.root.RootRouter;
import com.uber.rib.core.RibActivity;
import com.uber.rib.core.ViewRouter;

public class MainActivity extends RibActivity {

  @SuppressWarnings("unchecked")
  @Override
  protected ViewRouter<?, ?, ?> createRouter(ViewGroup parentViewGroup) {
    RootBuilder rootBuilder = new RootBuilder(new RootBuilder.ParentComponent() {});
    RootRouter router = rootBuilder.build(parentViewGroup);
    return router;
  }
}
