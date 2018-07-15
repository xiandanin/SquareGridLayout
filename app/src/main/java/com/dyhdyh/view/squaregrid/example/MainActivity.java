package com.dyhdyh.view.squaregrid.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        count=5;
        clickTest(null);

        RecyclerView.LayoutManager lm = new WeiBoLayoutManager();
        rv.setLayoutManager(lm);

        /*SquareGridLayout layout = findViewById(R.id.layout);
        layout.setAdapter(new NineGridImageViewAdapter() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, Object o) {
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            data.add("1");
        }
        layout.setImagesData(data);*/
    }

    int count = 0;

    public void clickTest(View view) {
        count++;
        List<String> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            data.add("ii");
        }
        rv.setAdapter(new TextAdapter(data));
    }
}
