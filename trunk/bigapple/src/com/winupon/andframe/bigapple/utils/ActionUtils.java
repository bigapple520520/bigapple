/* 
 * @(#)CallSmsUtils.java    Created on 2013-7-3
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 调用本地的一些操作，例如：发短信，打电话等
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-7-3 上午10:08:05 $
 */
public abstract class ActionUtils {
    /**
     * 调用短信程序发送短信
     * 
     * @param context
     */
    public static void sendSms(Context context) {
        ActionUtils.sendSmsByPhoneAndContext(context, null, null);
    }

    /**
     * 根据手机号发送短信
     * 
     * @param context
     * @param phone
     */
    public static void sendSmsByPhone(Context context, String phone) {
        ActionUtils.sendSmsByPhoneAndContext(context, phone, null);
    }

    /**
     * 根据内容调用手机通讯录
     * 
     * @param context
     * @param content
     */
    public static void sendSmsByContent(Context context, String content) {
        ActionUtils.sendSmsByPhoneAndContext(context, null, content);
    }

    /**
     * 初始化手机号和内容
     * 
     * @param context
     * @param phone
     * @param content
     */
    public static void sendSmsByPhoneAndContext(Context context, String phone, String content) {
        phone = TextUtils.isEmpty(phone) ? "" : phone;
        content = TextUtils.isEmpty(content) ? "" : content;

        Uri uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        context.startActivity(intent);
    }

    /**
     * 根据手机好拨打电话
     * 
     * @param context
     * @param phone
     */
    public static void callByPhone(Context context, String phone) {
        if (TextUtils.isEmpty(phone)) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

}
