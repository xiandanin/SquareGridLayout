package com.dyhdyh.view.squaregrid;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * @author dengyuhan
 *         created 2018/7/16 01:07
 */
public class WeiBoSquareGridAdapter extends SquareGridLayout.Adapter {
    private List<String> data;

    public WeiBoSquareGridAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType) {
        ImageView iv = new ImageView(parent.getContext());
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return iv;
    }

    @Override
    public void onBindViewHolder(View itemView, int position, List<Object> payloads) {
        ((ImageView)itemView).setImageResource(R.drawable.ic_launcher);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
