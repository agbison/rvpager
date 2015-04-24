
package io.bison.widget.recyclerViewPagerDemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.bison.widget.rvpager.LockLinearLayoutManager;
import io.bison.widget.rvpager.RecyclerViewPager;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;


public class RVLayoutFragment extends Fragment {
    private static final String ARG_LAYOUT_ID = "layout_id";

    private RecyclerViewPager mRecyclerView;
    private TextView mPositionText;
    private TextView mCountText;
    private TextView mStateText;

    private int mLayoutId;

    public static RVLayoutFragment newInstance(int layoutId) {
        RVLayoutFragment fragment = new RVLayoutFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        fragment.setArguments(args);

        return fragment;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = getArguments().getInt(ARG_LAYOUT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(mLayoutId, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Activity activity = getActivity();


        LockLinearLayoutManager layout = new LockLinearLayoutManager(getActivity(), OrientationHelper.HORIZONTAL, false);
        mRecyclerView = (RecyclerViewPager) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(new RVLayoutAdapter(activity, mRecyclerView, mLayoutId));
        mRecyclerView.setDisplayPadding(dip2px(getActivity(), 0));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.startAutoScroll();
        mPositionText = (TextView) view.getRootView().findViewById(R.id.position);
        mCountText = (TextView) view.getRootView().findViewById(R.id.count);
        mStateText = (TextView) view.getRootView().findViewById(R.id.state);
        updateState(SCROLL_STATE_IDLE);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                updateState(scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int childCount = mRecyclerView.getChildCount();
                int width = mRecyclerView.getChildAt(0).getWidth();
                int padding  = (mRecyclerView.getWidth() - width)/2;
                mCountText.setText("Count: " + mRecyclerView.getCenterXChildPosition());

//              // implement for old styling where it gets smaller like original CTVA looks
//                for (int j = 0; j < childCount; j++) {
//                    View v = recyclerView.getChildAt(j);
//
//                    float rate = 0;
//                    if (v.getLeft() <= padding) {
//                        if (v.getLeft() >= padding - v.getWidth()) {
//                            rate = (padding - v.getLeft()) * 1f / v.getWidth();
//                        } else {
//                            rate = 1;
//                        }
//                        v.setScaleY(1 - rate * 0.1f);
//                    } else {
//                        if (v.getLeft() <= recyclerView.getWidth() - padding) {
//                            rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
//                        }
//                        v.setScaleY(0.9f + rate * 0.1f);
//                    }
//                }
            }
        });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mRecyclerView.getChildAt(1) != null) {
                    View v2 = mRecyclerView.getChildAt(1);
                    v2.setScaleY(1.0f);
                    mRecyclerView.removeOnLayoutChangeListener(this);
                }
            }
        });

    }

    private void updateState(int scrollState) {
        String stateName = "Undefined";
        switch(scrollState) {
            case SCROLL_STATE_IDLE:
                stateName = "Idle";
                break;

            case SCROLL_STATE_DRAGGING:
                stateName = "Dragging";
                break;

            case SCROLL_STATE_SETTLING:
                stateName = "Flinging";
                break;
        }

        mStateText.setText(stateName);
    }

    public int getLayoutId() {
        return getArguments().getInt(ARG_LAYOUT_ID);
    }
}
