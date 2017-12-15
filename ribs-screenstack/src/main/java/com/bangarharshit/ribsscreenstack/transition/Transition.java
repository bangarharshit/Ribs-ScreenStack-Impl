package com.bangarharshit.ribsscreenstack.transition;

import android.view.View;
import com.bangarharshit.ribsscreenstack.ScreenStackImpl;

public interface Transition {

  void animate(View from, View to, ScreenStackImpl.Direction direction, Callback callback);

  interface Callback {
    void onAnimationEnd();
  }

}
