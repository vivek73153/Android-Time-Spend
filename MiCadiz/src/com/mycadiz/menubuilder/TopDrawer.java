package com.mycadiz.menubuilder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mycadiz.src.R;

public class TopDrawer extends VerticalDrawer {

    private int mIndicatorLeft;

    TopDrawer(Activity activity, int dragMode) {
        super(activity, dragMode);
    }

    public TopDrawer(Context context) {
        super(context);
    }

    public TopDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void openMenu(boolean animate) {
        animateOffsetTo(mMenuSize, 0, animate);
    }

    @Override
    public void closeMenu(boolean animate) {
        animateOffsetTo(0, 0, animate);
    }

    @Override
    public void setDropShadowColor(int color) {
        final int endColor = color & 0x00FFFFFF;
        mDropShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[] {
                        color,
                        endColor,
                });
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;
        final int offsetPixels = (int) mOffsetPixels;

        mMenuContainer.layout(0, 0, width, mMenuSize);
        offsetMenu(offsetPixels);

        if (USE_TRANSLATIONS) {
            mContentContainer.layout(0, 0, width, height);
        } else {
            mContentContainer.layout(0, offsetPixels, width, height + offsetPixels);
        }
    }

    /**
     * Offsets the menu relative to its original position based on the position of the content.
     *
     * @param offsetPixels The number of pixels the content if offset.
     */
    private void offsetMenu(int offsetPixels) {
        if (mOffsetMenu && mMenuSize != 0) {
            final int menuSize = mMenuSize;
            final float openRatio = (menuSize - (float) offsetPixels) / menuSize;

            if (USE_TRANSLATIONS) {
                if (offsetPixels > 0) {
                    final int offset = (int) (0.25f * (-openRatio * menuSize));
                    mMenuContainer.setTranslationY(offset);
                } else {
                    mMenuContainer.setTranslationY(-menuSize);
                }

            } else {
                final int oldMenuTop = mMenuContainer.getTop();
                final int offset = (int) (0.25f * (-openRatio * menuSize)) - oldMenuTop;
                mMenuContainer.offsetTopAndBottom(offset);
                mMenuContainer.setVisibility(offsetPixels == 0 ? INVISIBLE : VISIBLE);
            }
        }
    }

    @Override
    protected void drawDropShadow(Canvas canvas, int offsetPixels) {
        final int width = getWidth();

        mDropShadowDrawable.setBounds(0, offsetPixels - mDropShadowSize, width, offsetPixels);
        mDropShadowDrawable.draw(canvas);
    }

    @Override
    protected void drawMenuOverlay(Canvas canvas, int offsetPixels) {
        final int width = getWidth();
        final float openRatio = ((float) offsetPixels) / mMenuSize;

        mMenuOverlay.setBounds(0, 0, width, offsetPixels);
        mMenuOverlay.setAlpha((int) (MAX_MENU_OVERLAY_ALPHA * (1.f - openRatio)));
        mMenuOverlay.draw(canvas);
    }

    @Override
    protected void drawIndicator(Canvas canvas, int offsetPixels) {
        if (mActiveView != null && isViewDescendant(mActiveView)) {
            Integer position = (Integer) mActiveView.getTag(R.id.mdActiveViewPosition);
            final int pos = position == null ? 0 : position;

            if (pos == mActivePosition) {
                final int menuHeight = mMenuSize;
                final int indicatorHeight = mActiveIndicator.getHeight();

                final float openRatio = ((float) offsetPixels) / menuHeight;

                mActiveView.getDrawingRect(mActiveRect);
                offsetDescendantRectToMyCoords(mActiveView, mActiveRect);
                final int indicatorWidth = mActiveIndicator.getWidth();

                final float interpolatedRatio = 1.f - INDICATOR_INTERPOLATOR.getInterpolation((1.f - openRatio));
                final int interpolatedHeight = (int) (indicatorHeight * interpolatedRatio);

                final int indicatorTop = offsetPixels - interpolatedHeight;
                if (mIndicatorAnimating) {
                    final int finalLeft = mActiveRect.left + ((mActiveRect.width() - indicatorWidth) / 2);
                    final int startLeft = mIndicatorStartPos;
                    final int diff = finalLeft - startLeft;
                    final int startOffset = (int) (diff * mIndicatorOffset);
                    mIndicatorLeft = startLeft + startOffset;
                } else {
                    mIndicatorLeft = mActiveRect.left + ((mActiveRect.width() - indicatorWidth) / 2);
                }

                canvas.save();
                canvas.clipRect(mIndicatorLeft, indicatorTop, mIndicatorLeft + indicatorWidth, offsetPixels);
                canvas.drawBitmap(mActiveIndicator, mIndicatorLeft, indicatorTop, null);
                canvas.restore();
            }
        }
    }

    @Override
    protected int getIndicatorStartPos() {
        return mIndicatorLeft;
    }

    @Override
    protected void initPeekScroller() {
        final int dx = mMenuSize / 3;
        mPeekScroller.startScroll(0, 0, dx, 0, PEEK_DURATION);
    }

    @Override
    protected void onOffsetPixelsChanged(int offsetPixels) {
        if (USE_TRANSLATIONS) {
            mContentContainer.setTranslationY(offsetPixels);
            offsetMenu(offsetPixels);
            invalidate();
        } else {
            mContentContainer.offsetTopAndBottom(offsetPixels - mContentContainer.getTop());
            offsetMenu(offsetPixels);
            invalidate();
        }
    }

    //////////////////////////////////////////////////////////////////////
    // Touch handling
    //////////////////////////////////////////////////////////////////////

    @Override
    protected boolean isContentTouch(MotionEvent ev) {
        return ev.getY() > mOffsetPixels;
    }

    @Override
    protected boolean onDownAllowDrag(MotionEvent ev) {
        return (!mMenuVisible && mInitialMotionY <= mTouchSize)
                || (mMenuVisible && mInitialMotionY >= mOffsetPixels);
    }

    @Override
    protected boolean onMoveAllowDrag(MotionEvent ev, float diff) {
        return (!mMenuVisible && mInitialMotionY <= mTouchSize && (diff > 0))
                || (mMenuVisible && mInitialMotionY >= mOffsetPixels);
    }

    @Override
    protected void onMoveEvent(float dx) {
        setOffsetPixels(Math.min(Math.max(mOffsetPixels + dx, 0), mMenuSize));
    }

    @Override
    protected void onUpEvent(MotionEvent ev) {
        final int offsetPixels = (int) mOffsetPixels;

        if (mIsDragging) {
            mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
            final int initialVelocity = (int) mVelocityTracker.getXVelocity();
            mLastMotionY = ev.getY();
            animateOffsetTo(mVelocityTracker.getYVelocity() > 0 ? mMenuSize : 0, initialVelocity,
                    true);

            // Close the menu when content is clicked while the menu is visible.
        } else if (mMenuVisible && ev.getY() > offsetPixels) {
            closeMenu();
        }
    }
}
