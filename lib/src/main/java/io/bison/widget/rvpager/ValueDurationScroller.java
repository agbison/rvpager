package io.bison.widget.rvpager;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by agbison on 4/10/15.
 */
public class ValueDurationScroller extends Scroller {


        private double scrollFactor = 1;

        public ValueDurationScroller(Context context) {
            super(context);
        }

        public ValueDurationScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public void setScrollDurationFactor(double scrollFactor) {
            this.scrollFactor = scrollFactor;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, (int)(duration * scrollFactor));
        }
    }

