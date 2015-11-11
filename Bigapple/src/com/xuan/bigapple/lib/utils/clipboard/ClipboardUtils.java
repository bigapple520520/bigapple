package com.xuan.bigapple.lib.utils.clipboard;

import android.content.Context;
import android.text.TextUtils;

import com.xuan.bigapple.lib.Bigapple;

/**
 * 复制黏贴工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-12 下午5:15:00 $
 */
public abstract class ClipboardUtils {
	/**
	 * 复制文本，兼容老版本
	 * 
	 * @param text
	 *            需要复制的文字
	 */
	public static void copyText(String text) {
		if (TextUtils.isEmpty(text)) {
			return;
		}

		android.text.ClipboardManager clipboard = (android.text.ClipboardManager) Bigapple
				.getApplicationContext().getSystemService(
						Context.CLIPBOARD_SERVICE);
		clipboard.setText(text);

		// if (SdkVersionUtils.isLower(SdkVersionUtils.SDK30_API11)) {
		// android.text.ClipboardManager clipboard =
		// (android.text.ClipboardManager) context
		// .getSystemService(Context.CLIPBOARD_SERVICE);
		// clipboard.setText(text);
		// } else {
		// ClipboardManager clipboard = (ClipboardManager) context
		// .getSystemService(Context.CLIPBOARD_SERVICE);
		// ClipData clipData = android.content.ClipData.newPlainText(text,
		// text);
		// clipboard.setPrimaryClip(clipData);
		// }
	}

	/**
	 * 黏贴文本，兼容老版本
	 * 
	 * @return
	 */
	public static String pasteText() {
		android.text.ClipboardManager clipboard = (android.text.ClipboardManager) Bigapple
				.getApplicationContext().getSystemService(
						Context.CLIPBOARD_SERVICE);
		if (clipboard.hasText()) {
			return clipboard.getText().toString();
		}

		// if (SdkVersionUtils.isLower(SdkVersionUtils.SDK30_API11)) {
		// android.text.ClipboardManager clipboard =
		// (android.text.ClipboardManager) context
		// .getSystemService(Context.CLIPBOARD_SERVICE);
		// if (clipboard.hasText()) {
		// return clipboard.getText().toString();
		// }
		// }
		// else {
		// ClipboardManager clipboard = (ClipboardManager)
		// context.getSystemService(Context.CLIPBOARD_SERVICE);
		// if (clipboard.hasPrimaryClip()) {
		// ClipDescription clipDescription =
		// clipboard.getPrimaryClipDescription();
		// if
		// (ClipDescription.MIMETYPE_TEXT_PLAIN.endsWith(clipDescription.getMimeType(0)))
		// {
		// ClipData clipData = clipboard.getPrimaryClip();
		// Item item = clipData.getItemAt(0);
		// return item.getText().toString();
		// }
		// }
		// }

		return "";
	}

}
