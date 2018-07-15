package com.dyhdyh.view.squaregrid;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author dengyuhan
 *         created 2018/7/16 01:03
 */
public abstract class SquareGridAdapter {

    public abstract View onCreateView(ViewGroup parent, int viewType);

    public void onBindViewHolder(View itemView, int position, List<Object> payloads) {

    }
    public int getItemViewType(int position) {
        return 0;
    }

    public abstract int getItemCount();
}
