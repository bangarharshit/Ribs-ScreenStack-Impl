package com.bangarharshit.ribsscreenstack.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.View;
import com.bangarharshit.ribsscreenstack.ScreenStack;

import static com.bangarharshit.ribsscreenstack.ScreenStack.Direction.BACKWARD;
import static com.bangarharshit.ribsscreenstack.ScreenStack.Direction.FORWARD;

public class DefaultTransition implements Transition {

  private final NavigationType navigationType;

  public DefaultTransition(
      NavigationType navigationType) {
    this.navigationType = navigationType;
  }

  @Override
  public final void animate(View from, View to, ScreenStack.Direction direction, final Callback callback) {
    AnimatorSet animator = createAnimator(from, to, direction);
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        callback.onAnimationEnd();
      }
    });
    animator.start();
  }

  private AnimatorSet createAnimator(View from, View to, ScreenStack.Direction direction) {
      Property<View, Float> axis;
    int fromTranslation;
    int toTranslation;
    int sign = direction.sign();

    switch (navigationType) {
      case GO:
        axis = View.TRANSLATION_X;
        fromTranslation = sign * -from.getWidth();
        toTranslation = sign * to.getWidth();
        break;
      case SHOW:
        axis = View.TRANSLATION_Y;
        fromTranslation = direction == FORWARD ? 0 : from.getHeight();
        toTranslation = direction == BACKWARD ? 0 : to.getHeight();
        break;
      default:
        axis = View.TRANSLATION_X;
        fromTranslation = 0;
        toTranslation = 0;
        break;
    }
    AnimatorSet set = new AnimatorSet();
    if (from != null) {
      set.play(ObjectAnimator.ofFloat(from, axis, 0, fromTranslation));
    }
    set.play(ObjectAnimator.ofFloat(to, axis, toTranslation, 0));
    return set;
  }

  public enum NavigationType {
    GO,
    SHOW
  }
}
