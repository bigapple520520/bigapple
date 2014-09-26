/* 
 * @(#)ImageContainer.java    Created on 2014-9-22
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap.callback;

import android.graphics.Bitmap;

/**
 * 显示图片的容器，可以是任何能显示图片的组件
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-9-22 下午7:27:55 $
 */
public interface ImageContainer {

    /**
     * 显示图片
     * 
     * @param bitmap
     */
    void showImage(Bitmap bitmap);

}
