package com.bangarharshit.ribsscreenstack.transition;

import android.view.View;
import com.bangarharshit.ribsscreenstack.ScreenStack;

/**
 * No animation
 */
public class NoAnimationTransition implements Transition {

  @Override
  public void animate(
      View from, View to, ScreenStack.Direction direction, Callback callback) {
    callback.onAnimationEnd();
  }

}
