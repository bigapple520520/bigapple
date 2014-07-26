/* 
 * @(#)AnActivity.java    Created on 2012-11-29
 * Copyright (c) 2012 ZDSoft Networks, Inc. All rights reserved.
 * $Id: AnActivity.java 33154 2012-12-09 08:28:10Z xuan $
 */
package com.winupon.andframe.bigapple.ioc.app;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.winupon.andframe.bigapple.ioc.InjectParamThis;
import com.winupon.andframe.bigapple.ioc.InjectView;

/**
 * 此类继承了Activity，继承此类后，那么在该Activity内的所有标识了注解的属性View都会被自动注入。<br>
 * 如果不想继承此类，可以使用ViewUtils注入在代替。
 * 
 * @author xuan
 * @version $Revision: 33154 $, $Date: 2012-12-09 16:28:10 +0800 (周日, 09 十二月 2012) $
 */
public class AnActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void setContentView(int layout) {
        super.setContentView(layout);
        initAn();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        initAn();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initAn();
    }

    // 对各种注解进行注入
    private void initAn() {
        Field[] fileds = getClass().getDeclaredFields();

        for (int i = 0; i < fileds.length; i++) {
            initInjectView(fileds[i]);
            initInjectParamThis(fileds[i]);
        }
    }

    // 注解了InjectView的字段注入
    private void initInjectView(Field field) {
        InjectView injectView = field.getAnnotation(InjectView.class);

        if (null != injectView) {
            try {
                View view = this.findViewById(injectView.value());
                if (null != view) {
                    field.setAccessible(true);
                    field.set(this, view);
                }
            }
            catch (Exception e) {
                Log.e(TAG, "注解[InjectView]注入异常，原因：" + e.getMessage(), e);
            }
        }
    }

    // 注解了InjectParamThis的字段注入
    private void initInjectParamThis(Field field) {
        InjectParamThis injectParamThis = field.getAnnotation(InjectParamThis.class);

        if (null != injectParamThis) {
            try {
                field.setAccessible(true);
                Constructor<?> constructor = injectParamThis.value().getConstructor(new Class[] { Context.class });
                field.set(this, constructor.newInstance(new Object[] { this }));
            }
            catch (Exception e) {
                Log.e(TAG, "注解[InjectParamThis]注入异常，原因：" + e.getMessage(), e);
            }
        }
    }

}
