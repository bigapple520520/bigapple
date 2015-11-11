package com.xuan.bigapple.lib.ioc.app;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.xuan.bigapple.lib.ioc.InjectView;
import com.xuan.bigapple.lib.utils.log.LogUtils;

import java.lang.reflect.Field;

/**
 * 此类继承了FragmentActivity，继承此类后，那么在该Activity内的所有标识了注解的属性View都会被自动注入。<br>
 * 如果不想继承此类，可以使用ViewUtils注入在代替。
 *
 * @author xuan
 * @version $Revision: 33154 $, $Date: 2012-12-09 16:28:10 +0800 (周日, 09 十二月
 *          2012) $
 */
public class BPFragmentActivity extends FragmentActivity {
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

    /**
     * 对各种注解进行注入
     */
    private void initAn() {
        Field[] fileds = getClass().getDeclaredFields();

        for (int i = 0; i < fileds.length; i++) {
            initInjectView(fileds[i]);
        }
    }

    /**
     * 注解了InjectView的字段注入
     */
    private void initInjectView(Field field) {
        InjectView injectView = field.getAnnotation(InjectView.class);

        if (null != injectView) {
            try {
                View view = this.findViewById(injectView.value());
                if (null != view) {
                    field.setAccessible(true);
                    field.set(this, view);
                }
            } catch (Exception e) {
                LogUtils.e("BPInjectView exception. Cause:" + e.getMessage(), e);
            }
        }
    }

}
