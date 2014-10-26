/*
 *  sokoban - a Sokoban game for android devices
 *  Copyright (C) 2010 Dedi Hirschfeld
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.dio.sokoban;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageControl extends LinearLayout {
  /**
   * 初始化PageControl时圆点默认位置
   * */
  public static final int DEFAULT_POSITION = -1;
  private Context context;
  private int current;
  private int count;
  private OnPageControlListener onPageControlListener;
  private OnClickListener onClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      if (onPageControlListener != null) {
        onPageControlListener.onPageControl(v.getTag());
      }
    }
  };

  public PageControl(Context context, int count, int current) {
    super(context);
    this.context = context;
    this.count = count;
    this.current = current;
    moveToPosition(current);
  }

  public void setOnPageControlListener(OnPageControlListener onPageControlListener) {
    this.onPageControlListener = onPageControlListener;
  }

  public void moveToPosition(int position) {
    if (current == position && position != DEFAULT_POSITION) {
      return;
    }
    removeAllViews();
    current = position == DEFAULT_POSITION ? 0 : position;
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
    params.setMargins(6, 10, 6, 10);
    ImageView imageView;
    for (int i = 0; i < count; i++) {
      imageView = new ImageView(context);
      imageView
          .setBackgroundResource(current == i ? R.drawable.page_indicator_focused
              : R.drawable.page_indicator);
      imageView.setTag(i);
      imageView.setOnClickListener(onClickListener);
      addView(imageView, params);
    }
  }
  
  public interface OnPageControlListener {
    void onPageControl(Object tag);
  }
}
