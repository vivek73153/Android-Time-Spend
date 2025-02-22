/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eosos.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;


public class IjoomerIndexableListView extends ListView {

	private boolean mIsFastScrollEnabled = false;
	private IjoomerIndexScroller mScroller = null;
	private GestureDetector mGestureDetector = null;


	public IjoomerIndexableListView(Context context) {
		super(context);
	}

	public IjoomerIndexableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IjoomerIndexableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	@Override
	public void setFastScrollEnabled(boolean enabled) {

		mIsFastScrollEnabled = enabled;
		if (mIsFastScrollEnabled) {
			if (mScroller == null)
				mScroller = new IjoomerIndexScroller(getContext(), this);
		} else {
			if (mScroller != null) {
				mScroller.hide();
				mScroller = null;
			}
		}
	}

	public void onRefresh() {

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		// Overlay index bar
		if (mScroller != null) {
			mScroller.draw(canvas);
			mScroller.show();
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Intercept ListView's touch event
		if (mScroller != null && mScroller.onTouchEvent(ev)) {

			return true;
		}

		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
				int swipe_Min_Distance = 30;
				int swipe_Max_Distance = 350;
				int swipe_Min_Velocity = 30;

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
					// If fling happens, index bar shows
					final float xDistance = Math.abs(e1.getX() - e2.getX());
					final float yDistance = Math.abs(e1.getY() - e2.getY());

					if (xDistance > this.swipe_Max_Distance || yDistance > this.swipe_Max_Distance)
						return false;

					velocityX = Math.abs(velocityX);
					velocityY = Math.abs(velocityY);
					boolean result = false;

					if (velocityY > this.swipe_Min_Velocity && yDistance > this.swipe_Min_Distance) {
						if (e1.getY() > e2.getY()) // bottom to up
						{

						}
						else{
						}
						result = true;
					}

					return result;
				}

			});
		}
		mGestureDetector.onTouchEvent(ev);

		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mScroller != null)
			mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}

}
