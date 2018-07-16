package com.dyhdyh.view.squaregrid;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * @author dengyuhan
 *         created 2018/7/16 10:27
 */
public class SquareGridRecyclerAdapter extends SquareGridLayout.Adapter<SquareGridRecyclerAdapter.SquareGridRecyclerViewHolder> {
    private RecyclerView.Adapter mInnerAdapter;

    public SquareGridRecyclerAdapter(RecyclerView.Adapter adapter) {
        this.mInnerAdapter = adapter;
    }

    @Override
    public SquareGridRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new SquareGridRecyclerViewHolder(mInnerAdapter.onCreateViewHolder(parent, position));
    }

    @Override
    public void onBindViewHolder(SquareGridRecyclerViewHolder holder, int position) {
        mInnerAdapter.onBindViewHolder(holder.mInnerHolder, position);
    }

    @Override
    public int getItemCount() {
        return mInnerAdapter.getItemCount();
    }


    public static class SquareGridRecyclerViewHolder extends SquareGridLayout.ViewHolder {
        RecyclerView.ViewHolder mInnerHolder;

        public SquareGridRecyclerViewHolder(RecyclerView.ViewHolder holder) {
            super(holder.itemView);
            this.mInnerHolder = holder;
        }

    }
}
