package com.bangarharshit.ribsscreenstack.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import com.bangarharshit.ribsscreenstack.ScreenStack;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Cross Fades 2 views.
 */
public class CrossFadeTransition implements Transition {

  @Override
  public void animate(
      final View from, final View to, ScreenStack.Direction direction, final Callback callback) {

    to.setAlpha(0f);
    to.setVisibility(VISIBLE);

    from.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        from.setVisibility(GONE);
        to.animate().alpha(1f).setListener(new AnimatorListenerAdapter() {
          @Override public void onAnimationEnd(Animator animation) {
            callback.onAnimationEnd();
          }
        }).start();
      }
    }).start();
  }

}
