package com.xuan.bigapple.lib.utils;

import android.content.Context;
import android.content.Intent;

import com.xuan.bigapple.lib.utils.log.LogUtils;

public class ImitateKeyDownUtils {
	/**
	 * 执行命令
	 * 
	 * @param keyCommand
	 */
	public static void exec(String keyCommand) {
		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(keyCommand);
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
		}
	}

	/**
	 * 模拟Home键，用上面那种方式不灵验
	 */
	public static void homeKeyDown(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 提示:如果是服务里调用，必须加入new
														// task标识
		intent.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(intent);
	}

}
