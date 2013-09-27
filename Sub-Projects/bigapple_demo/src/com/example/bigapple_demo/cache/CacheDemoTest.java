/* 
 * @(#)CacheDemo.java    Created on 2013-8-20
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.example.bigapple_demo.cache;

import java.util.Date;

import android.content.Context;
import android.os.Handler;

import com.winupon.andframe.bigapple.cache.AnCacheUtils;
import com.winupon.andframe.bigapple.cache.Cache;
import com.winupon.andframe.bigapple.cache.CacheObject;
import com.winupon.andframe.bigapple.utils.ToastUtils;

/**
 * 缓存测试demo
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-20 下午4:12:57 $
 */
public abstract class CacheDemoTest {

    /**
     * 缓存对象测试
     * 
     * @param context
     */
    public static void getObjectCacheTest(Context context) {
        Cache<String, Object> cache = AnCacheUtils.getObjectMemoryCache();// 取到的是单例模式

        // 先取，木有
        showObject(context, cache.get("xuan"));

        // 存
        cache.put("xuan", "你好");

        // 再取，就有了
        showObject(context, cache.get("xuan"));

        // 清理缓存
        cache.removeAll();

        // 再取，木有
        showObject(context, cache.get("xuan"));
    }

    /**
     * 缓存对象测试，带会过期的
     * 
     * @param context
     * @param handler
     */
    public static void getObjectCacheTest2(final Context context, final Handler handler) {
        final Cache<String, Object> cache = AnCacheUtils.getObjectMemoryCache();// 取到的是单例模式

        // 存
        cache.put("xuan", new CacheObject<String>("你好", 2000));// 过期2S

        // 马上再取，就有了
        CacheObject<String> cacheObject = (CacheObject) cache.get("xuan");
        if (cacheObject.isExpired()) {
            ToastUtils.displayTextShort(context, "过期啦...");
        }
        else {
            ToastUtils.displayTextShort(context, cacheObject.getValue());
        }

        // 等缓存过期
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 暂停3S，是缓存过期去吧
                try {
                    Thread.sleep(3000);

                    // 再取，就过期啦
                    final CacheObject<String> temp = (CacheObject) cache.get("xuan");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (temp.isExpired()) {
                                ToastUtils.displayTextShort(context, "过期啦...");
                            }
                            else {
                                ToastUtils.displayTextShort(context, temp.getValue());
                            }
                        }
                    });
                }
                catch (Exception e) {
                }
            }
        }).start();
    }

    /**
     * 设置缓存过期时间测试
     * 
     * @param context
     * @param handler
     */
    public static void getObjectCacheTest3(final Context context, final Handler handler) {
        final Cache<String, Object> cache = AnCacheUtils.getObjectMemoryCache();// 取到的是单例模式

        // 存
        Date currentTime = new Date();
        cache.put("xuan", "你好", currentTime.getTime() + 2000);// 过期2S

        // 马上再取，就有了
        showObject(context, cache.get("xuan"));
        
        

        // 等缓存过期
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 暂停5S，是缓存过期去吧
                try {
                    Thread.sleep(5000);

                    // 再取，就过期啦
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showObject(context, cache.get("xuan"));
                        }
                    });
                }
                catch (Exception e) {
                }
            }
        }).start();
    }

    // 显示对象
    private static void showObject(Context context, Object object) {
        if (null == object) {
            ToastUtils.displayTextShort(context, "不好意思，缓存没取到");
        }
        else {
            ToastUtils.displayTextShort(context, (String) object);
        }
    }

}
