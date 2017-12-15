package com.bangarharshit.ribsscreenstack.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.View;
import com.bangarharshit.ribsscreenstack.ScreenStackImpl;

public class DefaultTransition implements Transition {

  @Override
  public final void animate(View from, View to, ScreenStackImpl.Direction direction, final Callback callback) {
    AnimatorSet animator = createAnimator(from, to, direction);
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        callback.onAnimationEnd();
      }
    });
    animator.start();
  }

  private AnimatorSet createAnimator(View from, View to, ScreenStackImpl.Direction direction) {
    Property<View, Float> axis = View.TRANSLATION_X;
    AnimatorSet set = new AnimatorSet();
    if (from != null) {
      set.play(ObjectAnimator.ofFloat(from, axis, 0, 0));
    }
    set.play(ObjectAnimator.ofFloat(to, axis, 0, 0));
    return set;
  }
}
