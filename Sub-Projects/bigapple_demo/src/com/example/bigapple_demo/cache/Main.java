package com.example.bigapple_demo.cache;

import android.app.Activity;
import android.os.Bundle;
<<<<<<< HEAD
=======
import android.os.Handler;
>>>>>>> a313c8ff32a2ec988896e6025368f863bb24ab28

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
<<<<<<< HEAD
=======
    private final Handler handler = new Handler();

>>>>>>> a313c8ff32a2ec988896e6025368f863bb24ab28
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
<<<<<<< HEAD

        String cachedStr = "我需要被缓存";
        AnCacheUtils.getObjectMemoryCache().put("key", cachedStr, System.currentTimeMillis() + 2000);

        try {
            Thread.sleep(2500);// 使缓存过期
        }
        catch (Exception e) {
        }

        String getCachedStr = (String) AnCacheUtils.getObjectMemoryCache().get("key");
        if (null != getCachedStr && cachedStr.equals(getCachedStr)) {
            ToastUtils.displayTextShort(this, getCachedStr);
        }
        else {
            ToastUtils.displayTextShort(this, "没有获取到缓存");
        }
    }
=======
        // CacheDemoTest.getObjectCacheTest3(this, handler);

        String dddd = (String) AnCacheUtils.getObjectMemoryCache().get("xa");
        if (null == dddd) {
            ToastUtils.displayTextShort(this, "ffff");
        }

    }

>>>>>>> a313c8ff32a2ec988896e6025368f863bb24ab28
}
