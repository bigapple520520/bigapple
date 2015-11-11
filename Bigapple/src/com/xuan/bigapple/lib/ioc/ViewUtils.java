package com.xuan.bigapple.lib.ioc;

import android.app.Activity;
import android.view.View;

import com.xuan.bigapple.lib.utils.log.LogUtils;

import java.lang.reflect.Field;

/**
 * 自动注入xml布局中组件的工具类，一般可以在Activity中的onCreate方法中调用。<br>
 * 例如：ViewUtils.inject(this)。<br>
 * 这样在这个Activity中用注解（@InjectView）标识的View，都会被自动注入。
 *
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-10-28 下午5:35:12 $
 */
public abstract class ViewUtils {
    /**
     * 注解注入View
     *
     * @param activity 使用当前的activity对象，注入当前activity中注解标识的View
     */
    public static void inject(Activity activity) {
        inject(activity, activity);
    }

    /**
     * 注解注入View
     *
     * @param handler 需要注入View的当前对象
     * @param view    蕴含View的View对象，例如可以用：LayoutInflater.from(this).inflate(xml,null
     *                );来从xml中产生一个View对象
     */
    public static void inject(Object handler, View view) {
        injectObject(handler, new ViewFinder(view));
    }

    /**
     * 注解注入View
     *
     * @param handler 需要注入View的当前对象
     * @param activity    蕴含View的Activity对象
     */
    public static void inject(Object handler, Activity activity) {
        injectObject(handler, new ViewFinder(activity));
    }

    /**
     * 注入View
     */
    private static void injectObject(Object handler, ViewFinder finder) {
        Field[] fields = handler.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                InjectView injectView = field.getAnnotation(InjectView.class);
                if (null != injectView) {
                    try {
                        View view = finder.findViewById(injectView.value());
                        if (null != view) {
                            field.setAccessible(true);
                            field.set(handler, view);
                        }
                    } catch (Exception e) {
                        LogUtils.e("注解注入[" + injectView.value() + "]View异常，原因："
                                + e.getMessage(), e);
                    }
                }
            }
        }
    }

}
