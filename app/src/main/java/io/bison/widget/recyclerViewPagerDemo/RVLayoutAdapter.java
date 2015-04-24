
package io.bison.widget.recyclerViewPagerDemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RVLayoutAdapter extends RecyclerView.Adapter<RVLayoutAdapter.SimpleViewHolder> {
    private static final int COUNT = 5;

    private static Context mContext;
    private final RecyclerView mRecyclerView;
    private final List<Integer> mItems;
    private final int mLayoutId;
    private int mCurrentItemId = 0;
    private static final String LOG_TAG = RVLayoutAdapter.class.getSimpleName();


    public static class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView title;
        public final ImageView imageView;

        public SimpleViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.title);
            imageView = (ImageView) view.findViewById(R.id.image);
            Picasso.with(mContext).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRFyu2jVcEqeoxZ2TOtWlCenPJ1flwbO8-seyVarHqy6do2E6Zu").into(imageView);



        }
        @Override
        public void onClick(View view) {
            Log.d(LOG_TAG,  "onClick " + getPosition() );
        }
    }

    public RVLayoutAdapter(Context context, RecyclerView recyclerView, int layoutId) {
        mContext = context;
        mItems = new ArrayList<Integer>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            addItem(i);
        }

        mRecyclerView = recyclerView;
        mLayoutId = layoutId;
    }

    public void addItem(int position) {
        final int id = mCurrentItemId++;
        mItems.add(position, id);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.title.setText(mItems.get(position).toString());
        final View itemView = holder.itemView;
        final int itemId = mItems.get(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }



}
