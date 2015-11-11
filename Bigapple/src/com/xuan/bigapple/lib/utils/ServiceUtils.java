package com.xuan.bigapple.lib.utils;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.xuan.bigapple.lib.Bigapple;

/**
 * 服务工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-25 上午9:14:13 $
 */
public class ServiceUtils {

	/**
	 * 判断service是否正在运行
	 * 
	 * @param className
	 *            service类名称
	 * @return
	 */
	public static boolean isServiceRunning(String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) Bigapple
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		if (serviceList.size() <= 0) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}

		return isRunning;
	}

	/**
	 * 启动服务，不会重复启动，但是由于检查会失去一些性能
	 * 
	 * @param clazz
	 * @param intent
	 */
	public static void startService(Class<?> clazz, Intent intent) {
		if (!isServiceRunning(clazz.getName())) {
			Bigapple.getApplicationContext().startService(intent);
		}
	}

	/**
	 * 关闭服务，会判断服务是否启动着，但是由于检查会失去一些性能
	 * 
	 * @param clazz
	 */
	public static void stopService(Class<?> clazz) {
		if (isServiceRunning(clazz.getName())) {
			Bigapple.getApplicationContext().stopService(
					new Intent(Bigapple.getApplicationContext(), clazz));
		}
	}

}
