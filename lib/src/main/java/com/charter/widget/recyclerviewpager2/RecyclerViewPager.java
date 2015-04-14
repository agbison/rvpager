package com.charter.widget.recyclerviewpager2;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerViewEx;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by agchtr on 4/10/15.
 */

public class RecyclerViewPager extends RecyclerViewEx {

    private static final String LOG_TAG = RecyclerViewPager.class.getSimpleName();
    public static final int   DEFAULT_INTERVAL = 8000;
    public static final int  LEFT = 0;
    public static final int  RIGHT = 1;
    public static final int  SLIDE_MODE_NONE      = 0;
    public static final int  SLIDE_MODE_CYCLE     = 1;
    public static final int  SLIDE_MODE_TO_PARENT = 2;
    private long  interval = DEFAULT_INTERVAL;
    private int direction   = RIGHT;
    private boolean isCycle    = true;
    private boolean stopScrollWhenTouch = true;
    private int slideMode = SLIDE_MODE_NONE;
    private double autoScrollFactor  = 1.0;
    private double  swipeScrollFactor  = 1.0;
    private boolean isAutoScroll = false;
    private boolean isStopByTouch = false;
    private float  touchX  = 0f, downX = 0f;
    private ValueDurationScroller scroller = null;
    public static final int SCROLL_NONE = 0;
    public static final int FLING_SCALE_DOWN_FACTOR = 0;
    public static final int FLING_SCALE_SIDE_FACTOR = 0;
    public static final int totalCount = 100; //MAGIC NUMBER FOR DEMO
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
        sendScrollMessage((long)(interval + scroller.getDuration() / autoScrollFactor * swipeScrollFactor));
    }

    public void startAutoScroll(int delayTimeInMills) {
        isAutoScroll = true;
        sendScrollMessage(delayTimeInMills);
    }

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

            scroller = new ValueDurationScroller(getContext(), (Interpolator)interpolatorField.get(null));
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
        } else if (nextItem == totalCount) {
            if (isCycle) {
                scrollToPosition(0);
                Log.d(LOG_TAG, "End of List");
            }
        } else {
                scrollToPosition(+currentItem);
                Log.d(LOG_TAG, "Is Auto Scrolling to position: " + getCenterXChildPosition());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);

        if (stopScrollWhenTouch) {
            if ((action == MotionEvent.ACTION_DOWN) && isAutoScroll) {
                isStopByTouch = true;
                stopAutoScroll();
            } else if (ev.getAction() == MotionEvent.ACTION_UP && isStopByTouch) {
                startAutoScroll();
            }
        }

        if (slideMode == SLIDE_MODE_TO_PARENT || slideMode == SLIDE_MODE_CYCLE) {
            touchX = ev.getX();
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                downX = touchX;
            }
            int currentItem = getCenterXChildPosition();
            RecyclerViewPagerAdapter adapter = getWrapperAdapter();
            int pageCount = adapter == null ? 0 : adapter.getItemCount();
            if ((currentItem == 0 && downX <= touchX) || (currentItem == pageCount - 1 && downX >= touchX)) {
                if (slideMode == SLIDE_MODE_TO_PARENT) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    if (pageCount > 1) {
                        scrollToPosition(pageCount - currentItem - 1);
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.dispatchTouchEvent(ev);
            }
        }
        getParent().requestDisallowInterceptTouchEvent(true);

        return super.dispatchTouchEvent(ev);
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
        mViewPagerAdapter = new RecyclerViewPagerAdapter(this,adapter);
        super.setAdapter(mViewPagerAdapter);
    }

    @Override
    public Adapter getAdapter() {
        if(mViewPagerAdapter!=null){
            return mViewPagerAdapter.mAdapter;
        }
        return null;
    }

   //Change Fling values for scroll event to fling instead, setting to zero here for view pager like function
    @Override
    public boolean fling(int velocityX, int velocityY)
    {
        velocityY *= FLING_SCALE_DOWN_FACTOR; // (between 0 for no fling, and 1 for normal fling, or more for faster fling).
        velocityX *= FLING_SCALE_SIDE_FACTOR;
        return super.fling(velocityX, velocityY);
    }

    public RecyclerViewPagerAdapter getWrapperAdapter(){
        return mViewPagerAdapter;
    }

    public int getDisplayPadding(){
        return mDisplayPadding;
    }

    public void setDisplayPadding(int displayPadding){
         mDisplayPadding = displayPadding;
    }
}
