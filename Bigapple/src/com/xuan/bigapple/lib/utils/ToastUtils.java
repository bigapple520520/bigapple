package com.xuan.bigapple.lib.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.xuan.bigapple.lib.Bigapple;

/**
 * 吐司信息工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-25 下午7:40:05 $
 */
public abstract class ToastUtils {
	/**
	 * 显示吐司信息（较长时间）
	 * 
	 * @param text
	 *            要提示的文本
	 */
	public static void displayTextLong(final String text) {
		if (null == text) {
			return;
		}

		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(Bigapple.getApplicationContext(), text,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * 显示吐司信息（较长时间）
	 * 
	 * @param context
	 *            上下文
	 * @param text
	 *            要提示的文本
	 */
	@Deprecated
	public static void displayTextLong(final Context context, final String text) {
		if (null == text) {
			return;
		}

		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * 显示吐司信息（较短时间），可以在任意的线程中调用
	 * 
	 * @param text
	 *            要提示的文本
	 */
	public static void displayTextShort(final String text) {
		if (null == text) {
			return;
		}

		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(Bigapple.getApplicationContext(), text,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 显示吐司信息（较短时间），可以在任意的线程中调用
	 * 
	 * @param context
	 *            上下文
	 * @param text
	 *            要提示的文本
	 */
	@Deprecated
	public static void displayTextShort(final Context context, final String text) {
		if (null == text) {
			return;
		}

		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
