package io.bison.widget.rvpager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by agbison on 4/10/15.
 */

public class RecyclerViewPagerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final RecyclerViewPager mRecyclerViewPager;
    RecyclerView.Adapter<VH> mAdapter;
    int mRecyclerPagerLeftMargin = -1;
    int mRecyclerPagerRightMargin = -1;


    public RecyclerViewPagerAdapter(RecyclerViewPager viewPager, RecyclerView.Adapter<VH> adapter) {
        mAdapter = adapter;
        mRecyclerViewPager = viewPager;
        setHasStableIds(mAdapter.hasStableIds());
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        mAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        mAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        mAdapter.onBindViewHolder(holder, position);
        final View itemView = holder.itemView;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
        if(mRecyclerPagerLeftMargin<0){
            mRecyclerPagerLeftMargin = lp.leftMargin;
        }
        if(mRecyclerPagerRightMargin<0){
            mRecyclerPagerRightMargin = lp.rightMargin;
        }
        int padding = mRecyclerViewPager.getDisplayPadding();
        lp.width = mRecyclerViewPager.getWidth() - 2*padding;
        if (position == 0) {
            if (itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                lp.leftMargin = mRecyclerPagerLeftMargin + padding;
                lp.rightMargin = mRecyclerPagerRightMargin;
            }
        } else if (position == getItemCount() - 1) {
            if (itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                lp.rightMargin = mRecyclerPagerRightMargin + padding;
                lp.leftMargin = mRecyclerPagerLeftMargin;
            }
        } else {
            if(lp.leftMargin>=mRecyclerPagerLeftMargin+padding) {
                lp.leftMargin = mRecyclerPagerLeftMargin;
            }
            if(lp.rightMargin>=mRecyclerPagerRightMargin+padding) {
                lp.rightMargin = mRecyclerPagerRightMargin;
            }
        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        mAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }


}
