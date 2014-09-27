/* 
 * @(#)AnBitmapUtilsFactory.java    Created on 2013-9-17
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.zzdemo.bitmap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.winupon.andframe.bigapple.bitmap.AnBitmapUtils;
import com.winupon.andframe.bigapple.bitmap.BitmapDisplayConfig;
import com.winupon.andframe.bigapple.bitmap.callback.ImageLoadCallBack;
import com.winupon.andframe.bigapple.bitmap.download.Downloader;

/**
 * 加载本地图片使用配置建议
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午4:59:56 $
 */
public class AnBitmapUtilsFace2 {
    private AnBitmapUtils anBitmapUtils;
    private static AnBitmapUtilsFace2 instance;// 单例模式
    
    private static BitmapDisplayConfig displayConfig;

    private AnBitmapUtilsFace2(Context application) {
    	anBitmapUtils = new AnBitmapUtils(application);
    	anBitmapUtils.DUBEG = true;
        anBitmapUtils.getGlobalConfig().setDiskCacheEnabled(false);//不开启磁盘缓存
        anBitmapUtils.getGlobalConfig().setMemoryCacheEnabled(true);
        anBitmapUtils.getGlobalConfig().setDiskCacheSize(1024*1024*8);
        
        displayConfig = new BitmapDisplayConfig();
        displayConfig.setImageLoadCallBack(new ImageLoadCallBack() {
			@Override
			public void loadFailed(ImageView imageView, Bitmap bitmap) {
				imageView.setImageBitmap(bitmap);
			}
			
			@Override
			public void loadCompleted(ImageView imageView, Bitmap bitmap,
					BitmapDisplayConfig config) {
				imageView.setImageBitmap(bitmap);
			}
		});
    }

    /**
     * 初始化，请在程序起来的时候初始化，即Application启动，可以多次调用最终只初始化一次
     * 
     * @param context
     */
    public synchronized static AnBitmapUtilsFace2 init(Context context) {
        if (null == context) {
            throw new NullPointerException("初始化context不能为空");
        }
        
        Application application = null;
        if (context instanceof Activity) {
            application = ((Activity) context).getApplication();
        }
        else if (context instanceof Application) {
            application = (Application) context;
        }

        if (null == instance) {
            instance = new AnBitmapUtilsFace2(application);
            //displayConfig.setBitmapMaxHeight(100);
            //displayConfig.setBitmapMaxWidth(100);
        }

        return instance;
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static AnBitmapUtilsFace2 getInstance() {
        instanceNullCheck();
        return AnBitmapUtilsFace2.instance;
    }

    // 判断实例是否是空
    private static void instanceNullCheck() {
        if (null == AnBitmapUtilsFace2.instance) {
            throw new RuntimeException("请先初始化AnBitmapUtils实例，方法：在程序启动的时候调用AnBitmapUtilsFace.init方法！");
        }
    }

    /**
     * 显示图片
     * 
     * @param imageView
     * @param uri
     */
    public void display(ImageView imageView, String uri) {
    	anBitmapUtils.display(imageView, uri,displayConfig);
    }

    /**
     * 清理所有缓存
     * 
     * @param afterClearCacheListener
     *            缓存清理成功后的回调，如果不想处理，设置成null也可
     */
    public void clearCacheAll() {
    	anBitmapUtils.clearCache();
    }

}
