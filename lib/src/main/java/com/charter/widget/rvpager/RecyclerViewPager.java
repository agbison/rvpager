package com.charter.widget.rvpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by agchtr on 4/10/15.
 */

public class RecyclerViewPager extends RecyclerView {

    private static final String LOG_TAG = RecyclerViewPager.class.getSimpleName();
    public static final int DEFAULT_INTERVAL = 8000;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int SLIDE_MODE_NONE = 0;
    public static final int SLIDE_MODE_CYCLE = 1;
    public static final int SLIDE_MODE_TO_PARENT = 2;
    private long interval = DEFAULT_INTERVAL;
    private int direction = RIGHT;
    private boolean isCycle = true;
    private int slideMode = SLIDE_MODE_TO_PARENT;
    private double autoScrollFactor = 1.0;
    private double swipeScrollFactor = 1.0;
    private boolean isAutoScroll = false;
    private boolean isStopByTouch = true;
    private ValueDurationScroller scroller = null;
    public static final int SCROLL_NONE = 0;
    private Handler mHandler;
    public int mDisplayPadding;

    RecyclerViewPagerAdapter mViewPagerAdapter;

    public RecyclerViewPager(Context context) {
        super(context);
        init();
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mHandler = new MyHandler(this);
        setViewPagerScroller();
    }

    public void startAutoScroll() {
        isAutoScroll = true;
        sendScrollMessage((long) (interval + scroller.getDuration() / autoScrollFactor * swipeScrollFactor));
    }
    //Don't use this API set globally per design leaving since it works fine
//    public void startAutoScroll(int delayTimeInMills) {
//        isAutoScroll = true;
//        sendScrollMessage(delayTimeInMills);
//    }

    public void stopAutoScroll() {
        isAutoScroll = false;
        mHandler.removeMessages(SCROLL_NONE);
    }

    public void setAutoScrollDurationFactor(double scrollFactor) {
        autoScrollFactor = scrollFactor;
    }

    public void setSwipeScrollDurationFactor(double scrollFactor) {
        swipeScrollFactor = scrollFactor;
    }

    private void sendScrollMessage(long delayTimeInMills) {
        mHandler.removeMessages(SCROLL_NONE);
        mHandler.sendEmptyMessageDelayed(SCROLL_NONE, delayTimeInMills);
    }

    private void setViewPagerScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);

            scroller = new ValueDurationScroller(getContext(), (Interpolator) interpolatorField.get(null));
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scrollOnce() {
        int currentItem = getCenterXChildPosition();
        int nextItem = (direction == LEFT) ? --currentItem : ++currentItem;
        if (nextItem < 0) {
            if (isCycle) {
                scrollToPosition(0);
                Log.d(LOG_TAG, "Cycling");
            }
        } else if (nextItem == mViewPagerAdapter.getItemCount()) {
            if (isCycle) {
                scrollToPosition(0);
                Log.d(LOG_TAG, "End of List");
            }
        } else {
            scrollToPosition(+currentItem);
            Log.d(LOG_TAG, "Is Auto Scrolling to position: " + getCenterXChildPosition());
        }
    }


    private static class MyHandler extends Handler {

        private final WeakReference<RecyclerViewPager> autoScrollViewPager;

        public MyHandler(RecyclerViewPager autoScrollViewPager) {
            this.autoScrollViewPager = new WeakReference<>(autoScrollViewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SCROLL_NONE:
                    RecyclerViewPager pager = this.autoScrollViewPager.get();
                    if (pager != null) {
                        pager.scroller.setScrollDurationFactor(pager.autoScrollFactor);
                        pager.scrollOnce();
                        pager.scroller.setScrollDurationFactor(pager.swipeScrollFactor);
                        pager.sendScrollMessage(pager.interval + pager.scroller.getDuration());
                    }
                default:
                    break;
            }
        }
    }

    public long getInterval() {
        return interval;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        super.swapAdapter(adapter, removeAndRecycleExistingViews);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mViewPagerAdapter = new RecyclerViewPagerAdapter(this, adapter);
        super.setAdapter(mViewPagerAdapter);
    }

    @Override
    public Adapter getAdapter() {
        if (mViewPagerAdapter != null) {
            return mViewPagerAdapter.mAdapter;
        }
        return null;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        final LayoutManager lm = getLayoutManager();
        if (lm instanceof LockLayoutManager) {
            super.smoothScrollToPosition(((LockLayoutManager) getLayoutManager())
                    .getPositionForVelocity(velocityX, velocityY));
            return true;
        }
        return super.fling(velocityX, velocityY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        final boolean ret = super.onTouchEvent(e);
        final LayoutManager lm = getLayoutManager();

            if (lm instanceof LockLayoutManager
                    && (e.getAction() == MotionEvent.ACTION_UP ||
                    e.getAction() == MotionEvent.ACTION_CANCEL)
                    && getScrollState() == SCROLL_STATE_IDLE) {
                smoothScrollToPosition(((LockLayoutManager) lm).getFixScrollPos());
            }

            return ret;
        }


    public RecyclerViewPagerAdapter getWrapperAdapter() {
        return mViewPagerAdapter;
    }

    public int getDisplayPadding() {
        return mDisplayPadding;
    }

    public void setDisplayPadding(int displayPadding) {
        mDisplayPadding = displayPadding;
    }

    public int getCenterXChildPosition() {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (isChildInCenterX(child)) {
                    return getChildPosition(child);
                }
            }
        }
        return childCount;
    }

    public boolean isChildInCenterX(View view) {
        int childCount = getChildCount();
        int[] lvLocationOnScreen = new int[2];
        int[] vLocationOnScreen = new int[2];
        getLocationOnScreen(lvLocationOnScreen);
        int middleX = lvLocationOnScreen[0] + getWidth() / 2;
        if (childCount > 0) {
            view.getLocationOnScreen(vLocationOnScreen);
            if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.getWidth() >= middleX) {
                return true;
            }
        }
        return false;
    }

}
