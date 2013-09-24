package com.example.bigapple_demo.bitmap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.bigapple_demo.R;

/**
 * 本http访问需要权限：<br>
 * <uses-permission android:name="android.permission.INTERNET" /><br>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-8 下午3:33:41 $
 */
public class Main extends Activity {
    private Button loadButton;
    private Button clearButton;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bitmap_layout_test);

        loadButton = (Button) findViewById(R.id.loadButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        loadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnBitmapUtilsDemo.loadBitmapDefault(Main.this, imageView);
            }
        });

        clearButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnBitmapUtilsDemo.clearCache(Main.this);
            }
        });
    }

}
