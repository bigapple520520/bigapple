package com.example.bigapple_demo.utils.display;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.bigapple_demo.R;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 简单的日志工具测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-18 下午3:37:24 $
 */
public class Main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        DisplayMetrics metrics = DisplayUtils.getDisplayMetrics(this);
        LogUtils.d("--------------------density:" + metrics.density);
        LogUtils.d("--------------------densityDpi:" + metrics.densityDpi);
        LogUtils.d("--------------------heightPixels:" + metrics.heightPixels);
        LogUtils.d("--------------------scaledDensity:" + metrics.scaledDensity);
        LogUtils.d("--------------------widthPixels:" + metrics.widthPixels);
        LogUtils.d("--------------------xdpi:" + metrics.xdpi);
        LogUtils.d("--------------------ydpi:" + metrics.ydpi);

        LogUtils.d("--------------------ydpi:" + DisplayUtils.getDpByPx(this, 50));
        LogUtils.d("--------------------ydpi:" + DisplayUtils.getPxByDp(this, 50));
        LogUtils.d("--------------------ydpi:" + DisplayUtils.getPxBySp(this, 50));
        LogUtils.d("--------------------ydpi:" + DisplayUtils.getSpByPx(this, 50));
    }
}
