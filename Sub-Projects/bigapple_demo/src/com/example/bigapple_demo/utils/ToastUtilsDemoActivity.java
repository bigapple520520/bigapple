package com.example.bigapple_demo.utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.bigapple_demo.R;
import com.winupon.andframe.bigapple.utils.ToastUtils;

public class ToastUtilsDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_utils_toastutilsdemoactivity);
    }

    // 显示普通toast
    public void showToast(View view) {
        ToastUtils.displayTextShort(this, "我是普通的Toast哦");
    }

}
