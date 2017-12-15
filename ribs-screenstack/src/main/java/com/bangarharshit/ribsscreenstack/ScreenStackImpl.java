package com.bangarharshit.ribsscreenstack;

import android.os.Parcelable;
import android.support.annotation.UiThread;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.bangarharshit.ribsscreenstack.transition.Transition;
import com.uber.rib.core.screenstack.ScreenStackBase;
import com.uber.rib.core.screenstack.ViewProvider;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.bangarharshit.ribsscreenstack.ScreenStackImpl.Direction.FORWARD;
import static com.bangarharshit.ribsscreenstack.Views.whenMeasured;

@UiThread
public class ScreenStackImpl implements ScreenStackBase {

  private final Deque<StateFulViewProvider> backStack = new ArrayDeque<>();
  private View ghostView; // keep track of the disappearing view we are animating
  private final ViewGroup parentViewGroup;
  private final Transition defaultTransiton;
  private Transition overridingTransition;

  public ScreenStackImpl(ViewGroup parentViewGroup, Transition defaultTransiton) {
    this.parentViewGroup = parentViewGroup;
    this.defaultTransiton = defaultTransiton;
  }

  public void setOverridingTransition(Transition overridingTransition) {
    this.overridingTransition = overridingTransition;
  }

  @Override public void pushScreen(final ViewProvider viewProvider) {
    pushScreen(viewProvider, false);
  }

  @Override public void pushScreen(final ViewProvider viewProvider, boolean shouldAnimate) {
    navigate(new Runnable() {
      @Override public void run() {
        backStack.push(new StateFulViewProvider(viewProvider));
      }
    }, FORWARD);
  }

  @Override public void popScreen() {
    popScreen(false);
  }

  @Override public void popScreen(boolean shouldAnimate) {
    navigate(new Runnable() {
      @Override public void run() {
        backStack.pop();
      }
    }, Direction.BACKWARD);
  }

  @Override public void popBackTo(final int index, boolean shouldAnimate) {
    navigate(new Runnable() {
      @Override public void run() {
        if (index > size() || index < -1) {
          throw new IllegalArgumentException("Index size invalid");
        }
        while (size() - 1 > index) {
          backStack.pop();
        }
      }
    }, Direction.BACKWARD);
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

  private void navigate(final Runnable backStackOperation, final Direction direction) {
    View from = removeCurrentScreen();
    saveCurrentState(from);
    backStackOperation.run();
    View to = showCurrentScreen(direction);
    animateAndRemove(from, to, direction);
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
    if (isAnimating()) {
      parentViewGroup.removeView(ghostView);
      ghostView = null;
    }
    return parentViewGroup.getChildAt(0);
  }

  private boolean isAnimating() {
    return ghostView != null;
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
      final View from, final View to, final Direction direction) {
    // This is the first view pushed.
    if (from == null) {
      return;
    }
    // This is the last view removed.
    if (to == null) {
      parentViewGroup.removeView(from);
      return;
    }
    ghostView = from;
    final Transition transitionToUse = overridingTransition != null ? overridingTransition : defaultTransiton;
    overridingTransition = null;
    whenMeasured(to, new Views.OnMeasured() {
      @Override
      public void onMeasured() {
        transitionToUse.animate(from, to, direction, new Transition.Callback() {
          @Override
          public void onAnimationEnd() {
            if (parentViewGroup != null) {
              parentViewGroup.removeView(from);
              if (from == ghostView) {
                // Only clear the ghost if it's the same as the view we just removed
                ghostView = null;
              }
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
