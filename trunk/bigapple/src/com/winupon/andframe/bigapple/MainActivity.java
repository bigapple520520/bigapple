package com.winupon.andframe.bigapple;

import android.os.Bundle;

import com.winupon.andframe.bigapple.ioc.AnActivity;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

public class MainActivity extends AnActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtils.d("1111111111111111");
        LogUtils.allowD = false;
        LogUtils.d("dug test");
    }

}
