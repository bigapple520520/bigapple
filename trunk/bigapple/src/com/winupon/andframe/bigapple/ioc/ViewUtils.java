/* 
 * @(#)ViewUtils.java    Created on 2013-10-28
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.ioc;

import java.lang.reflect.Field;

import android.app.Activity;
import android.view.View;

import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 注解注入View
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-10-28 下午5:35:12 $
 */
public abstract class ViewUtils {

    public static void inject(Activity activity) {
        injectObject(activity, new ViewFinder(activity));
    }

    public static void inject(Object handler, View view) {
        injectObject(handler, new ViewFinder(view));
    }

    public static void inject(Object handler, Activity activity) {
        injectObject(handler, new ViewFinder(activity));
    }

    private static void injectObject(Object handler, ViewFinder finder) {
        Field[] fields = handler.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                InjectView injectView = field.getAnnotation(InjectView.class);
                if (null != injectView) {
                    try {
                        field.setAccessible(true);
                        field.set(handler, finder.findViewById(injectView.value()));
                    }
                    catch (Exception e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                }
            }
        }
    }

}
