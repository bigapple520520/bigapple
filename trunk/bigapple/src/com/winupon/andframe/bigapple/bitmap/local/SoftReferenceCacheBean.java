/* 
 * @(#)DefaultCacheBean.java    Created on 2014-10-31
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap.local;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;

import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 默认的缓存持有方式，因为软引用很容易被回收，所以如果不想使用软引用，使用者可以自己实现这个接口来修改缓存的持有方式
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-10-31 下午1:31:16 $
 */
public class SoftReferenceCacheBean implements CacheBean {
    private SoftReference<Bitmap> bitmapReference;

    @Override
    public void setBitmap(Bitmap bm) {
        if (null == bm) {
            LogUtils.e("notice!!!you put the bitamp to cache is null!!!");
            return;
        }

        bitmapReference = new SoftReference<Bitmap>(bm);
    }

    @Override
    public Bitmap getBitmap() {
        if (null == bitmapReference) {
            LogUtils.e("notice!!!get get the bitmapReference is null!!!");
            return null;
        }

        return bitmapReference.get();
    }

}
