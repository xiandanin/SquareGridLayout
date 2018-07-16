package com.dyhdyh.view.squaregrid.example;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyhdyh.view.squaregrid.SquareGridLayout;

import java.util.List;

/**
 * @author dengyuhan
 *         created 2018/7/16 11:35
 */
public class TestAdapter extends SquareGridLayout.Adapter<TestAdapter.ItemViewHolder> {
    List<String> data;

    public TestAdapter(List<String> data) {
        this.data = data;
    }


    @Override
    public TestAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.iv.setImageResource(R.drawable.test_image);
        holder.tv.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ItemViewHolder extends SquareGridLayout.ViewHolder {
        private ImageView iv;
        private TextView tv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }
}
