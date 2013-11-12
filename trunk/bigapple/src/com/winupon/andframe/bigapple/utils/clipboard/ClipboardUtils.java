/* 
 * @(#)ClipboardManagerUtils.java    Created on 2013-11-12
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.clipboard;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.winupon.andframe.bigapple.utils.SdkVersionUtils;

/**
 * 复制黏贴工具
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-12 下午5:15:00 $
 */
@TargetApi(11)
public abstract class ClipboardUtils {

    /**
     * 复制文本，兼容老版本
     * 
     * @param context
     * @param text
     */
    public static void copyText(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        if (SdkVersionUtils.isLower(SdkVersionUtils.SDK30_API11)) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        }
        else {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = android.content.ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clipData);
        }
    }

    /**
     * 黏贴文本，兼容老版本
     * 
     * @param context
     * @param label
     * @param text
     */
    public static String pasteText(Context context) {
        if (SdkVersionUtils.isLower(SdkVersionUtils.SDK30_API11)) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasText()) {
                return clipboard.getText().toString();
            }
        }
        else {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()) {
                ClipDescription clipDescription = clipboard.getPrimaryClipDescription();
                if (ClipDescription.MIMETYPE_TEXT_PLAIN.endsWith(clipDescription.getMimeType(0))) {
                    ClipData clipData = clipboard.getPrimaryClip();
                    Item item = clipData.getItemAt(0);
                    return item.getText().toString();
                }
            }
        }

        return "";
    }

}
