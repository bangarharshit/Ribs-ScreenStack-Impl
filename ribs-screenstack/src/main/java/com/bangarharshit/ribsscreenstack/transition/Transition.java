package com.bangarharshit.ribsscreenstack.transition;

import android.view.View;
import com.bangarharshit.ribsscreenstack.ScreenStack;

/**
 * Base class for different transition.
 */
public interface Transition {

  void animate(View from, View to, ScreenStack.Direction direction, Callback callback);

  /**
   * Callback to indicate animation ended. It is used by {@link ScreenStack }to remove from view.
   */
  interface Callback {
    void onAnimationEnd();
  }

}
