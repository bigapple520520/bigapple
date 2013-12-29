package com.example.bigapple_demo.cache;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.example.bigapple_demo.R;
import com.winupon.andframe.bigapple.cache.AnCacheUtils;
import com.winupon.andframe.bigapple.utils.ToastUtils;

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
        // CacheDemoTest.getObjectCacheTest3(this, handler);

        String dddd = (String) AnCacheUtils.getObjectMemoryCache().get("xa");
        if (null == dddd) {
            ToastUtils.displayTextShort(this, "ffff");
        }

    }

}
