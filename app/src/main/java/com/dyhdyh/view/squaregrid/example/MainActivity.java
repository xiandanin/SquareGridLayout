package com.dyhdyh.view.squaregrid.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dyhdyh.view.squaregrid.SquareGridLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SquareGridLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);

        clickTest(null);
    }

    int count = 0;

    public void clickTest(View view) {
        count++;
        List<String> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            data.add("ii");
        }
        layout.setAdapter(new TestAdapter(data), SquareGridLayout.WEIBO);
        layout.notifyDataSetChanged();
    }
}
