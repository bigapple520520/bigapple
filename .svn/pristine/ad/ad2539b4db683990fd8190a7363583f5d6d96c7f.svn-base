/* 
 * @(#)IntentUtils.java    Created on 2014-4-25
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils;

import android.content.Context;
import android.content.Intent;

/**
 * 利用Intent调用本地应用
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-4-25 下午5:30:10 $
 */
public abstract class IntentUtils {

    /**
     * 调转分享
     * 
     * @param context
     * @param subject
     * @param text
     * @param title
     */
    public static void gotoShare(Context context, String subject, String text, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "请选择"));
    }

}
