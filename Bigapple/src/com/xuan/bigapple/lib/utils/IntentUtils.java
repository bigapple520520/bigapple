package com.xuan.bigapple.lib.utils;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 调用本地的一些操作，例如：发短信，打电话等
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-7-3 上午10:08:05 $
 */
public abstract class IntentUtils {
	/**
	 * 调用短信程序发送短信
	 * 
	 * @param context
	 */
	public static void sendSms(Context context) {
		IntentUtils.sendSmsByPhoneAndContext(context, null, null);
	}

	/**
	 * 根据手机号发送短信
	 * 
	 * @param context
	 * @param phone
	 */
	public static void sendSmsByPhone(Context context, String phone) {
		IntentUtils.sendSmsByPhoneAndContext(context, phone, null);
	}

	/**
	 * 根据内容调用手机通讯录
	 * 
	 * @param context
	 * @param content
	 */
	public static void sendSmsByContent(Context context, String content) {
		IntentUtils.sendSmsByPhoneAndContext(context, null, content);
	}

	/**
	 * 初始化手机号和内容
	 * 
	 * @param context
	 * @param phone
	 * @param content
	 */
	public static void sendSmsByPhoneAndContext(Context context, String phone,
			String content) {
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

		Intent intent = new Intent(Intent.ACTION_CALL,
				Uri.parse("tel:" + phone));
		context.startActivity(intent);
	}

	// ////////////////////////////////////调用系统自带的文件选择器////////////////////////////////////////////////
	/**
	 * 打开文件选择器
	 * 
	 * @param activity
	 *            Activity实例
	 * @param requestCode
	 *            onActivityResult返回的操作识别
	 * @return true成功false失败
	 */
	public static boolean showFileChooser(Activity activity, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			activity.startActivityForResult(
					Intent.createChooser(intent, "文件选择"), requestCode);
			return true;
		} catch (Throwable e) {
			LogUtils.e(e.getMessage(), e);
			return false;
		}
	}

	// ////////////////////////////////分享////////////////////////////////////////////
	/**
	 * 分享文本内容
	 * 
	 * @param context
	 * @param subject
	 * @param text
	 * @param title
	 */
	public static void share(Context context, String subject, String text,
			String title) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.putExtra(Intent.EXTRA_TITLE, title);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, "分享到"));
	}

	/**
	 * 可分享图片或者文本内容
	 * 
	 * @param context
	 * @param content
	 * @param uri
	 */
	public static void share(Context context, String content, Uri uri) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		if (uri != null) {
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			shareIntent.setType("image/*");
		} else {
			shareIntent.setType("text/plain");
		}
		shareIntent.putExtra(Intent.EXTRA_TEXT, content);
		context.startActivity(Intent.createChooser(shareIntent, "分享到"));
	}

	/**
	 * 可分享图片或者文本内容
	 * 
	 * @param context
	 * @param content
	 * @param uri
	 */
	public static void share(Context context, String content, String fileName) {
		if (!TextUtils.isEmpty(fileName)) {
			File file = new File(fileName);
			if (file.exists()) {
				share(context, content, Uri.fromFile(file));
			} else {
				LogUtils.e("图片[" + fileName + "]不存在");
			}
		} else {
			share(context, content, (Uri) null);
		}
	}

}
