package com.bangarharshit.ribsscreenstack.transition;

import android.view.View;
import com.bangarharshit.ribsscreenstack.ScreenStackImpl;

public class NoAnimationTransition implements Transition {

  @Override
  public void animate(
      View from, View to, ScreenStackImpl.Direction direction, Callback callback) {
    callback.onAnimationEnd();
  }

}
