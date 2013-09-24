package com.example.bigapple_demo.cache;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.example.bigapple_demo.R;

/**
 * 缓存模块测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-18 下午3:37:24 $
 */
public class Main extends Activity {
    private final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CacheDemoTest.getObjectCacheTest2(this, handler);
    }

}
