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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.harshitbangar.example.R;
import com.jakewharton.rxrelay2.PublishRelay;
import io.reactivex.Observable;

/** Top level view for {@link RootBuilder.RootScope}. */
public class RootView extends LinearLayout implements RootInteractor.RootPresenter {

  private PublishRelay<String> contentRelay = PublishRelay.create();
  public RootView(Context context) {
    this(context, null);
  }

  public RootView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RootView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    final EditText editText = findViewById(R.id.enter_name);
    Button nextButton = findViewById(R.id.next_button);
    nextButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        String content = editText.getText().toString();
        if (TextUtils.isEmpty(content)) {
          return;
        }
        contentRelay.accept(content);
      }
    });
  }

  @Override public Observable<String> pushScreen2() {
    return contentRelay.hide();
  }
}
