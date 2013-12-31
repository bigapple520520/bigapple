package com.example.bigapple_demo.utils.display;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * 屏幕显示工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-21 下午4:01:22 $
 */
public abstract class DisplayUtils {

    /**
     * 返回屏幕参数值，其中含：density、scaledDensity、densityDpi、heightPixels、widthPixels、xdpi、ydpi
     * 
     * @param activity
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    /**
     * dp转成px（也可以使用SDK自带的TypedValue工具类）
     * 
     * @param activity
     * @param dpValue
     * @return
     */
    public static float getPxByDp(Activity activity, float dpValue) {
        return dpValue * getDisplayMetrics(activity).density;
    }

    /**
     * px转成dp
     * 
     * @param activity
     * @param pxValue
     * @return
     */
    public static float getDpByPx(Activity activity, float pxValue) {
        return pxValue / getDisplayMetrics(activity).density;
    }

    /**
     * sp转成px
     * 
     * @param activity
     * @param spValue
     * @return
     */
    public static float getPxBySp(Activity activity, float spValue) {
        return spValue * getDisplayMetrics(activity).scaledDensity;
    }

    /**
     * px转成sp
     * 
     * @param activity
     * @param pxValue
     * @return
     */
    public static float getSpByPx(Activity activity, float pxValue) {
        return pxValue / getDisplayMetrics(activity).scaledDensity;
    }

}
