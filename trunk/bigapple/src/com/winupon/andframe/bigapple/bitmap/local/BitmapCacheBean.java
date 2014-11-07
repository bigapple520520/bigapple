/* 
 * @(#)BitmapCacheBean.java    Created on 2014-10-31
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap.local;

import android.graphics.Bitmap;

import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 缓存持有方式
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-10-31 下午2:23:19 $
 */
public class BitmapCacheBean implements CacheBean {
    private Bitmap bitmap;

    @Override
    public void setBitmap(Bitmap bm) {
        if (null == bm) {
            LogUtils.e("notice!!!you put the bitamp to cache is null!!!");
            return;
        }

        this.bitmap = bm;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

}
