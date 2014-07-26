package com.example.bigapple_demo.http;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private Button button;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_layout_test);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HttpTestDemo.downloadTest(textView);
                // HttpTestDemo.postTest(textView);
                // HttpTestDemo.postSyncTest(textView);
                // HttpTestDemo.getTest(textView);
                // HttpTestDemo.getSyncTest(textView);
                HttpTestDemo.uploadFileTest(textView);
            }
        });
    }

}
