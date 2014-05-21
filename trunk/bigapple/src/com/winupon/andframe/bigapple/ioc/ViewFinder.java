/* 
 * @(#)ViewFinder.java    Created on 2013-10-28
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.ioc;

import android.app.Activity;
import android.view.View;

/**
 * 注入View的寻找器
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-10-28 下午5:37:24 $
 */
public class ViewFinder {
    private View view;
    private Activity activity;

    public ViewFinder(View view) {
        this.view = view;
    }

    public ViewFinder(Activity activity) {
        this.activity = activity;
    }

    /**
     * 从寻找器中找到指定的View
     * 
     * @param id
     * @return
     */
    public View findViewById(int id) {
        return null == activity ? view.findViewById(id) : activity.findViewById(id);
    }

}
