/* 
 * @(#)ImageSpanUtils.java    Created on 2013-4-16
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.textviewhtml.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

/**
 * 可以处理图标和文字添加到EditText中
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-4-16 下午7:50:24 $
 */
public abstract class ImageSpanUtils {

    /**
     * 图片代替文字
     * 
     * @param context
     * @param text
     * @param resId
     * @return
     */
    public static SpannableString getSpannableStringByTextReplaceBitmap(Context context, String text, int resId) {
        Drawable drawable = context.getResources().getDrawable(resId);
        BitmapDrawable bd = (BitmapDrawable) drawable;

        ImageSpan imageSpan = new ImageSpan(context, bd.getBitmap());

        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(imageSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    /**
     * 图片代替文字
     * 
     * @param context
     * @param text
     * @param bitmap
     * @return
     */
    public static SpannableString getSpannableStringByTextReplaceBitmap(Context context, String text, Bitmap bitmap) {
        ImageSpan imageSpan = new ImageSpan(context, bitmap);

        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(imageSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

}
