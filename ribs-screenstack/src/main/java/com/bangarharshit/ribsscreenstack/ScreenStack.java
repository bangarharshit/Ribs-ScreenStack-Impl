package com.bangarharshit.ribsscreenstack;

import android.os.Parcelable;
import android.support.annotation.UiThread;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.bangarharshit.ribsscreenstack.transition.NoAnimationTransition;
import com.bangarharshit.ribsscreenstack.transition.Transition;
import com.uber.rib.core.screenstack.ScreenStackBase;
import com.uber.rib.core.screenstack.ViewProvider;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.inject.Provider;

import static com.bangarharshit.ribsscreenstack.ScreenStack.Direction.FORWARD;
import static com.bangarharshit.ribsscreenstack.Views.whenMeasured;

/**
 * An implementation of {@link ScreenStackBase} with support for animation and state restoration.
 */
@UiThread
public final class ScreenStack implements ScreenStackBase {

  private final Deque<StateFulViewProvider> backStack = new ArrayDeque<>();
  private final ViewGroup parentViewGroup;
  private final Provider<Transition> defaultTransitionProvider;

  public ScreenStack(ViewGroup parentViewGroup,
      Provider<Transition> defaultTransitionProvider) {
    this.parentViewGroup = parentViewGroup;
    this.defaultTransitionProvider = defaultTransitionProvider;
  }

  public void pushScreen(final ViewProvider viewProvider, final Transition transition) {
    navigate(
        new Runnable() {
          @Override public void run() {
            backStack.push(new StateFulViewProvider(viewProvider));
          }
        },
        FORWARD,
        transition);
  }

  @Override public void pushScreen(final ViewProvider viewProvider) {
    pushScreen(viewProvider, defaultTransitionProvider.get());
  }

  @Override public void pushScreen(final ViewProvider viewProvider, boolean shouldAnimate) {
    pushScreen(viewProvider, shouldAnimate ? defaultTransitionProvider.get() : new NoAnimationTransition());
  }

  @Override public void popScreen() {
    popScreen(defaultTransitionProvider.get());
  }

  @Override public void popScreen(boolean shouldAnimate) {
    popScreen(shouldAnimate ? defaultTransitionProvider.get() : new NoAnimationTransition());
  }

  public void popScreen( final Transition transition) {
    navigate(
        new Runnable() {
          @Override public void run() {
            backStack.pop();
          }
        },
        Direction.BACKWARD,
        transition);
  }

  @Override public void popBackTo(final int index, boolean shouldAnimate) {
    popBackTo(index, shouldAnimate ? defaultTransitionProvider.get() : new NoAnimationTransition());
  }

  public void popBackTo(final int index, final Transition transition) {
    navigate(
        new Runnable() {
          @Override public void run() {
            if (index > size() || index < -1) {
              throw new IllegalArgumentException("Index size invalid");
            }
            while (size() - 1 > index) {
              backStack.pop();
            }
          }
        },
        Direction.BACKWARD,
        transition);
  }

  @Override public boolean handleBackPress() {
    return false;
  }

  @Override public boolean handleBackPress(boolean shouldAnimate) {
    return false;
  }

  @Override public int size() {
    return backStack.size();
  }

  private void navigate(final Runnable backStackOperation, final Direction direction, final Transition transition) {
    View from = removeCurrentScreen();
    saveCurrentState(from);
    backStackOperation.run();
    View to = showCurrentScreen(direction);
    animateAndRemove(from, to, direction, transition);
    restoreCurrentState(to);
  }

  private void restoreCurrentState(View currentView) {
    StateFulViewProvider stateFulViewProvider = currentStateFulViewProvider();
    if (stateFulViewProvider == null) {
      return;
    }
    currentView.restoreHierarchyState(stateFulViewProvider.parcelableSparseArray);
  }

  private void saveCurrentState(View currentView) {
    StateFulViewProvider stateFulViewProvider = currentStateFulViewProvider();
    if (stateFulViewProvider == null) {
      return;
    }
    currentView.saveHierarchyState(stateFulViewProvider.parcelableSparseArray);
  }

  private View removeCurrentScreen() {
    return parentViewGroup.getChildAt(0);
  }

  private View showCurrentScreen(final Direction direction) {
    StateFulViewProvider stateFulViewProvider = currentStateFulViewProvider();
    if (stateFulViewProvider == null) {
      return null;
    }
    View currentView = stateFulViewProvider.viewProvider.buildView(parentViewGroup);
    parentViewGroup.addView(currentView, direction == FORWARD ? parentViewGroup.getChildCount() : 0);
    return currentView;
  }

  private void animateAndRemove(
      final View from,
      final View to,
      final Direction direction,
      final Transition transitionToUse) {
    // This is the first view pushed.
    if (from == null) {
      return;
    }
    // This is the last view removed.
    if (to == null) {
      parentViewGroup.removeView(from);
      return;
    }
    whenMeasured(to, new Views.OnMeasured() {
      @Override
      public void onMeasured() {
        transitionToUse.animate(from, to, direction, new Transition.Callback() {
          @Override
          public void onAnimationEnd() {
            if (parentViewGroup != null) {
              parentViewGroup.removeView(from);
            }
          }
        });
      }
    });
  }

  private StateFulViewProvider currentStateFulViewProvider() {
    if (!backStack.isEmpty()) {
      return backStack.peek();
    }
    return null;
  }

  static class StateFulViewProvider {
    private final ViewProvider viewProvider;
    private final SparseArray<Parcelable> parcelableSparseArray;

    StateFulViewProvider(ViewProvider viewProvider) {
      this.viewProvider = viewProvider;
      this.parcelableSparseArray = new SparseArray<>();
    }
  }

  public enum Direction {

    FORWARD(1),
    BACKWARD(-1);

    private final int sign;

    Direction(int sign) {
      this.sign = sign;
    }

    public int sign() {
      return sign;
    }
  }
}
