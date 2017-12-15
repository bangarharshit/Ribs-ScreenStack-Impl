/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harshitbangar.example.root;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.harshitbangar.example.R;
import com.uber.rib.core.Bundle;
import com.uber.rib.core.Interactor;
import com.uber.rib.core.RibInteractor;
import com.uber.rib.core.screenstack.ScreenStackBase;
import com.uber.rib.core.screenstack.ViewProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import javax.inject.Inject;

/**
 * Coordinates Business Logic for {@link RootBuilder.RootScope}.
 */
@RibInteractor
public class RootInteractor extends Interactor<RootInteractor.RootPresenter, RootRouter> {

  @Inject RootPresenter presenter;
  @Inject ScreenStackBase screenStackBase;
  @Inject Context context;
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Override protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
    super.didBecomeActive(savedInstanceState);
    compositeDisposable.add(presenter
        .pushScreen2()
        .subscribe(new Consumer<String>() {
      @Override public void accept(String s) throws Exception {
        pushScreen2(s);
      }
    }));
  }

  @Override protected void willResignActive() {
    super.willResignActive();
    compositeDisposable.dispose();
  }

  private void pushScreen2(final String name) {
    screenStackBase.pushScreen(new ViewProvider() {
      @Override public View buildView(ViewGroup parentView) {
        View root = LayoutInflater.from(context).inflate(R.layout.screen_2, parentView, false);
        TextView textView = root.findViewById(R.id.text_view);
        textView.setText(name);
        Button nextButton = root.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            pushScreen3(name);
          }
        });
        return root;
      }
    });
  }

  private void pushScreen3(final String name) {
    screenStackBase.pushScreen(new ViewProvider() {
      @Override public View buildView(ViewGroup parentView) {
        View root = LayoutInflater.from(context).inflate(R.layout.screen_3, parentView, false);
        TextView textView = root.findViewById(R.id.text_view);
        textView.setText(name);
        Button nextButton = root.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            pushScreen4(name);
          }
        });
        return root;
      }
    });
  }

  private void pushScreen4(final String name) {
    screenStackBase.pushScreen(new ViewProvider() {
      @Override public View buildView(ViewGroup parentView) {
        View root = LayoutInflater.from(context).inflate(R.layout.screen_4, parentView, false);
        TextView textView = root.findViewById(R.id.text_view);
        textView.setText(name);
        Button popButton = root.findViewById(R.id.screen_2);
        popButton.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            screenStackBase.popBackTo(1, false);
          }
        });
        return root;
      }
    });
  }

  @Override public boolean handleBackPress() {
    if (screenStackBase.size() > 0) {
      screenStackBase.popScreen();
      return true;
    }
    return super.handleBackPress();
  }

  /**
   * Presenter interface implemented by this RIB's view.
   */
  interface RootPresenter {
    Observable<String> pushScreen2();
  }
}
