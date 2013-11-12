/* 
 * @(#)SdkVersionUtils.java    Created on 2013-11-12
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils;

import android.os.Build.VERSION;

/**
 * SDK版本判断
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-12 下午7:28:17 $
 */
public abstract class SdkVersionUtils {
    public static final int SDK10_API1 = 1;// BASE
    public static final int SDK11_API2 = 2;// BASE_1_1
    public static final int SDK15_API3 = 3;// CUPCAKE
    public static final int SDK16_API4 = 4;// CUR_DEVELOPMENT
    public static final int SDK20_API5 = 5;// ECLAIR
    public static final int SDK201_API6 = 6;// ECLAIR_0_1
    public static final int SDK21_API7 = 7;// ECLAIR_MR1
    public static final int SDK122_API8 = 8;// FROYO

    public static final int SDK23_API9 = 9;// GINGERBREAD
    public static final int SDK233_API10 = 10;// GINGERBREAD_MR1

    public static final int SDK30_API11 = 11;// HONEYCOMB
    public static final int SDK31_API12 = 12;// HONEYCOMB_MR1
    public static final int SDK32_API13 = 13;// HONEYCOMB_MR2

    public static final int SDK40_API14 = 14;// ICE_CREAM_SANDWICH
    public static final int SDK403_API15 = 15;// ICE_CREAM_SANDWICH_MR1
    public static final int SDK41_API16 = 16;// JELLY_BEAN
    public static final int SDK42_API17 = 17;// JELLY_BEAN_MR1
    public static final int SDK43_API18 = 18;// JELLY_BEAN_MR2

    public static int getSdkInt() {
        return VERSION.SDK_INT;
    }

    public static boolean isUpper(int sdkInt) {
        return VERSION.SDK_INT > sdkInt;
    }

    public static boolean isUpperEquals(int sdkInt) {
        return VERSION.SDK_INT >= sdkInt;
    }

    public static boolean isLower(int sdkInt) {
        return VERSION.SDK_INT < sdkInt;
    }

    public static boolean isLowerEquals(int sdkInt) {
        return VERSION.SDK_INT <= sdkInt;
    }

    public static boolean isEquals(int sdkInt) {
        return sdkInt == VERSION.SDK_INT;
    }

}
