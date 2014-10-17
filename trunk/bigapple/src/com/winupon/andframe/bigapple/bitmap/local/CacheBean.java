/* 
 * @(#)CacheBean.java    Created on 2014-10-15
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap.local;

import android.graphics.Bitmap;

/**
 * 缓存对象
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-10-15 上午9:43:19 $
 */
public interface CacheBean {

    /**
     * 设置bitmap
     * 
     * @param bm
     */
    void setBitmap(Bitmap bm);

    /**
     * 获取bitmap
     * 
     * @return
     */
    Bitmap getBitmap();

}
