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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollLayout extends ViewGroup {
  private Scroller mScroller;
  private VelocityTracker mVelocityTracker;

  private int mCurScreen;
  private int mDefaultScreen = 0;

  private static final int TOUCH_STATE_REST = 0;
  private static final int TOUCH_STATE_SCROLLING = 1;

  private static final int SNAP_VELOCITY = 600;

  private int mTouchState = TOUCH_STATE_REST;
  private int mTouchSlop;
  private float mLastMotionX;

  private OnSnapListener onSnapListener;

  public ScrollLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
    // TODO Auto-generated constructor stub
  }

  public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    // TODO Auto-generated constructor stub
    mScroller = new Scroller(context);

    mCurScreen = mDefaultScreen;
    mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    // TODO Auto-generated method stub
    if (changed) {
      int childLeft = 0;
      final int childCount = getChildCount();

      for (int i = 0; i < childCount; i++) {
        final View childView = getChildAt(i);
        if (childView.getVisibility() != View.GONE) {
          final int childWidth = childView.getMeasuredWidth();
          childView.layout(childLeft, 0, childLeft + childWidth,
              childView.getMeasuredHeight());
          childLeft += childWidth;
        }
      }
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    final int width = MeasureSpec.getSize(widthMeasureSpec);
    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    if (widthMode != MeasureSpec.EXACTLY) {
      throw new IllegalStateException(
          "ScrollLayout only canmCurScreen run at EXACTLY mode!");
    }

    final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    if (heightMode != MeasureSpec.EXACTLY) {
      throw new IllegalStateException(
          "ScrollLayout only can run at EXACTLY mode!");
    }

    // The children are given the same width and height as the scrollLayout
    final int count = getChildCount();
    for (int i = 0; i < count; i++) {
      getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
    }
    // Log.e(TAG, "moving to screen "+mCurScreen);
    scrollTo(mCurScreen * width, 0);
  }

  public void setOnSnapListener(OnSnapListener onSnapListener) {
    this.onSnapListener = onSnapListener;
  }

  /**
   * According to the position of current layout scroll to the destination page.
   */
  public void snapToDestination() {
    final int screenWidth = getWidth();
    final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
    snapToScreen(destScreen);
  }

  public void snapToScreen(int whichScreen) {
    // get the valid layout page
    whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
    if (getScrollX() != (whichScreen * getWidth())) {

      final int delta = whichScreen * getWidth() - getScrollX();
      mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
      mCurScreen = whichScreen;
      invalidate(); // Redraw the layout
      if (onSnapListener != null) {
        onSnapListener.onSnapComplete();
      }
    }
  }

  public void setToScreen(int whichScreen) {
    whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
    mCurScreen = whichScreen;
    scrollTo(whichScreen * getWidth(), 0);
    if (onSnapListener != null) {
      onSnapListener.onSnapComplete();
    }
  }

  public int getCurScreen() {
    return mCurScreen;
  }

  @Override
  public void computeScroll() {
    // TODO Auto-generated method stub
    if (mScroller.computeScrollOffset()) {
      scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
      postInvalidate();
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // TODO Auto-generated method stub

    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(event);

    final int action = event.getAction();
    final float x = event.getX();

    switch (action) {
    case MotionEvent.ACTION_DOWN:
      if (!mScroller.isFinished()) {
        mScroller.abortAnimation();
      }
      mLastMotionX = x;
      break;

    case MotionEvent.ACTION_MOVE:
      int deltaX = (int) (mLastMotionX - x);
      mLastMotionX = x;

      scrollBy(deltaX, 0);
      break;

    case MotionEvent.ACTION_UP:
      // if (mTouchState == TOUCH_STATE_SCROLLING) {
      final VelocityTracker velocityTracker = mVelocityTracker;
      velocityTracker.computeCurrentVelocity(1000);
      int velocityX = (int) velocityTracker.getXVelocity();

      if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
        // Fling enough to move left
        snapToScreen(mCurScreen - 1);
      } else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
        // Fling enough to move right
        snapToScreen(mCurScreen + 1);
      } else {
        snapToDestination();
      }
      if (mVelocityTracker != null) {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
      }
      // }
      mTouchState = TOUCH_STATE_REST;
      break;
    case MotionEvent.ACTION_CANCEL:
      mTouchState = TOUCH_STATE_REST;
      break;
    }

    return true;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    final int action = ev.getAction();
    if ((action == MotionEvent.ACTION_MOVE)
        && (mTouchState != TOUCH_STATE_REST)) {
      return true;
    }

    final float x = ev.getX();

    switch (action) {
    case MotionEvent.ACTION_MOVE:
      final int xDiff = (int) Math.abs(mLastMotionX - x);
      if (xDiff > mTouchSlop) {
        mTouchState = TOUCH_STATE_SCROLLING;

      }
      break;

    case MotionEvent.ACTION_DOWN:
      mLastMotionX = x;
      mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
          : TOUCH_STATE_SCROLLING;
      break;

    case MotionEvent.ACTION_CANCEL:
    case MotionEvent.ACTION_UP:
      mTouchState = TOUCH_STATE_REST;
      break;
    }
    return mTouchState != TOUCH_STATE_REST;
  }

  public interface OnSnapListener {
    void onSnapComplete();
  }
}