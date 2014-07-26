package com.winupon.andframe.bigapple.utils.log;

import android.util.Log;

/**
 * 一个简单的日志工具封装，可简单自己定位TAG，TAG的格式为：类名[方法名， 调用行数]
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-13 下午12:22:49 $
 */
public abstract class LogUtils {
    public static boolean allowD = true;
    public static boolean allowE = true;
    public static boolean allowI = true;
    public static boolean allowV = true;
    public static boolean allowW = true;
    public static boolean allowWtf = true;

    // d方法
    // -----------------------------------------------------------------------------------------------------------------
    public static void d(String content) {
        if (!allowD) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.d(tag, content);
    }

    public static void d(String content, Throwable tr) {
        if (!allowD) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.d(tag, content, tr);
    }

    // e方法
    // ------------------------------------------------------------------------------------------------------------------
    public static void e(String content) {
        if (!allowE) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.e(tag, content);
    }

    public static void e(String content, Throwable tr) {
        if (!allowE) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.e(tag, content, tr);
    }

    // i方法
    // -----------------------------------------------------------------------------------------------------------------
    public static void i(String content) {
        if (!allowI) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.i(tag, content);
    }

    public static void i(String content, Throwable tr) {
        if (!allowI) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.i(tag, content, tr);
    }

    // v方法
    // -----------------------------------------------------------------------------------------------------------------
    public static void v(String content) {
        if (!allowV) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.v(tag, content);
    }

    public static void v(String content, Throwable tr) {
        if (!allowV) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.v(tag, content, tr);
    }

    // w方法
    // -----------------------------------------------------------------------------------------------------------------
    public static void w(String content) {
        if (!allowW) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        if (!allowW) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.w(tag, content, tr);
    }

    public static void w(Throwable tr) {
        if (!allowW) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.w(tag, tr);
    }

    // wtf方法，非常恐怖的错误，这种错误原则上不应该出现在系统中，哈哈
    // -----------------------------------------------------------------------------------------------------------------
    public static void wtf(String content) {
        if (!allowWtf) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.wtf(tag, content);
    }

    public static void wtf(String content, Throwable tr) {
        if (!allowWtf) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.wtf(tag, content, tr);
    }

    public static void wtf(Throwable tr) {
        if (!allowWtf) {
            return;
        }
        StackTraceElement caller = getCallerMethodName();
        String tag = generateTag(caller);
        Log.wtf(tag, tr);
    }

    // 跟踪到调用该日志的方法
    private static StackTraceElement getCallerMethodName() {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        return stacks[4];
    }

    // 规范TAG格式：类名[方法名， 调用行数]
    private static String generateTag(StackTraceElement caller) {
        String tag = "%s[%s, %d]";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        return tag;
    }
}
