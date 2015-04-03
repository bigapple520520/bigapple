/* 
 * @(#)GlobalPolicy.java    Created on 2014-10-31
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap.core;

import com.winupon.andframe.bigapple.bitmap.local.CacheBean;
import com.winupon.andframe.bigapple.bitmap.local.SoftReferenceCacheBean;

/**
 * 全局策略，使用者可以改变该策略，来达到一些自定义的效果
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-10-31 下午1:50:04 $
 */
public class GlobalPolicy {

    /**
     * 获取bitmap的持有方式，如果不爽，复写他，自己返回一个继承CacheBean的实例
     */
    public CacheBean makeCacheBean() {
        return new SoftReferenceCacheBean();
    }

    /**
     * 获取缓存的key，如果想要自己特有的key，例如不想把url后面的参数带入当做key，可以复写他自己定制一个规则
     * 
     * @param url
     * @return
     */
    public String makeCacheKey(String url) {
        return url;
    }

}
