package com.example.bigapple_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.bigapple_demo.utils.ToastUtilsDemoActivity;

public class TestBigappleMain extends Activity {
    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        content = (LinearLayout) findViewById(R.id.content);

        addButton("Toast工具类", ToastUtilsDemoActivity.class);
    }

    private void addButton(String text, final Class<?> clazz) {
        Button button = new Button(this);
        button.setText(text);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(TestBigappleMain.this, clazz);
                startActivity(intent);
            }
        });
        content.addView(button);
    }

}
