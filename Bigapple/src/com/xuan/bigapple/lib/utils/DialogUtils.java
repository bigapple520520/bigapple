package com.xuan.bigapple.lib.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * 自带Dialog工具类
 * 
 * @author xuan
 * @version $Revision: 36102 $, $Date: 2013-03-20 12:33:56 +0800 (Wed, 20 Mar
 *          2013) $
 */
public class DialogUtils {

	/**
	 * 展现简单的一个按钮的alert框，类似网页alert
	 * 
	 * @param title
	 * @param message
	 * @param btnText
	 */
	public static void alert(final Context context, final String title,
			final String message, final String btnText) {
		if (null == context || !(context instanceof Activity)) {
			return;
		}
		final Activity activity = (Activity) context;
		AlertDialog alertDialog = new AlertDialog.Builder(activity)
				.setPositiveButton(btnText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).setTitle(title).setMessage(message).create();

		if (!activity.isFinishing()) {
			alertDialog.show();
		}
	}

	/**
	 * 供用户选择，然后触发事件的提示框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param message
	 *            提示文本
	 * @param positiveBtnText
	 *            确定按钮文本
	 * @param positionOnclick
	 *            确定按钮事件
	 * @param negativeBtnText
	 *            取消按钮文本
	 * @param negativeOnclick
	 *            取消按钮事件
	 */
	public static void comfirm(Context context, String title, String message,
			String positiveBtnText,
			DialogInterface.OnClickListener positionOnclick,
			String negativeBtnText,
			DialogInterface.OnClickListener negativeOnclick) {
		if (null == context || !(context instanceof Activity)) {
			return;
		}
		final Activity activity = (Activity) context;

		Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setMessage(message);

		if (null == positionOnclick) {
			positionOnclick = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};
		}
		builder.setPositiveButton(positiveBtnText, positionOnclick);

		if (null == negativeOnclick) {
			negativeOnclick = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};
		}
		builder.setNegativeButton(negativeBtnText, negativeOnclick);

		if (!activity.isFinishing()) {
			builder.create().show();
		}
	}

	/**
	 * 多个选择选一个
	 * 
	 * @param context
	 * @param title
	 * @param cancelable
	 * @param selectNames
	 * @param OnClickListener
	 */
	public static void select(final Context context, String title,
			boolean cancelable, String[] selectNames,
			final DialogInterface.OnClickListener onClickListener) {
		if (null == context || !(context instanceof Activity)) {
			return;
		}
		final Activity activity = (Activity) context;

		AlertDialog accountDlg = new AlertDialog.Builder(activity)
				.setTitle(title).setCancelable(cancelable)
				.setSingleChoiceItems(selectNames, -1, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (null != onClickListener) {
							onClickListener.onClick(dialog, which);
						}

						if (!activity.isFinishing()) {
							dialog.dismiss();
						}
					}
				}).create();

		if (!activity.isFinishing()) {
			accountDlg.show();
		}
	}

	/**
	 * 多个选择选一个
	 * 
	 * @param context
	 * @param title
	 * @param cancelable
	 * @param selectNames
	 * @param OnClickListener
	 */
	public static void select2(final Context context, String title,
			boolean cancelable, String[] selectNames,
			final DialogInterface.OnClickListener onClickListener) {
		if (null == context || !(context instanceof Activity)) {
			return;
		}
		final Activity activity = (Activity) context;

		AlertDialog accountDlg = new AlertDialog.Builder(activity)
				.setTitle(title).setCancelable(cancelable)
				.setItems(selectNames, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (null != onClickListener) {
							onClickListener.onClick(dialog, which);
						}

						if (!activity.isFinishing()) {
							dialog.dismiss();
						}
					}
				}).create();

		if (!activity.isFinishing()) {
			accountDlg.show();
		}
	}

}
