package com.xuan.bigapple.lib.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xuan.bigapple.lib.Bigapple;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 判断网络或者SD等之类的工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-25 上午9:22:02 $
 */
public abstract class ContextUtils {

	/**
	 * 判断是否存在网络连接
	 * 
	 * @return
	 */
	public static boolean hasNetwork() {
		ConnectivityManager connectManager = (ConnectivityManager) Bigapple
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();

		return null != networkInfo && networkInfo.isAvailable()
				&& networkInfo.isConnected();
	}

	/**
	 * 判断是否存在网络连接
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static boolean hasNetwork(Context context) {
		ConnectivityManager connectManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();

		return null != networkInfo && networkInfo.isAvailable()
				&& networkInfo.isConnected();
	}

	/**
	 * 判断GPS是否打开
	 * 
	 * @return
	 */
	public static boolean isGpsEnabled() {
		LocationManager locationManager = (LocationManager) Bigapple
				.getApplicationContext().getSystemService(
						Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * 判断GPS是否打开
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * SD卡是否可用
	 * 
	 * @return
	 */
	public static boolean hasSdCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 获取SD的根目录
	 * 
	 * @return
	 */
	public static String getSdCardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 获取手机本身的内置存储，一般SD卡不存在的时候使用。 /data/data/程序包名/cache
	 * 
	 * @return
	 */
	public static String getCacheDirPath() {
		return Bigapple.getApplicationContext().getCacheDir().getPath();
	}

	/**
	 * 获取手机本身的内置存储，一般SD卡不存在的时候使用。 /data/data/程序包名/cache
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static String getCacheDirPath(Context context) {
		return context.getCacheDir().getPath();
	}

	/**
	 * 获取手机本身的内置存储。 /data/data/程序包名/files
	 * 
	 * @return
	 */
	public static String getFileDirPath() {
		return Bigapple.getApplicationContext().getFilesDir().getPath();
	}

	/**
	 * 获取手机本身的内置存储。 /data/data/程序包名/files
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static String getFileDirPath(Context context) {
		return context.getFilesDir().getPath();
	}

	/**
	 * 获取SD默认缓存路径：/Android/data/程序包名/cache/
	 * 
	 * @return
	 */
	public static String getExternalCacheDirPath() {
		return Bigapple.getApplicationContext().getExternalCacheDir().getPath();
	}

	/**
	 * 获取SD默认缓存路径：/Android/data/程序包名/cache/
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static String getExternalCacheDirPath(Context context) {
		return context.getExternalCacheDir().getPath();
	}

	/**
	 * 是否有sim卡
	 * 
	 * @return
	 */
	public static boolean hasSimCard() {
		TelephonyManager telephonyManager = (TelephonyManager) Bigapple
				.getApplicationContext().getSystemService(
						Context.TELEPHONY_SERVICE);
		return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
	}

	/**
	 * 是否有sim卡
	 * 
	 * @return
	 */
	@Deprecated
	public static boolean hasSimCard(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
	}

	/**
	 * 显示或者隐藏键盘
	 * 
	 * @param view
	 * @param isShow
	 */
	public static void showSoftInput(View view, boolean isShow) {
		InputMethodManager imm = (InputMethodManager) Bigapple
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		if (isShow) {
			imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
		} else {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 显示软键盘
	 * 
	 * @param editText
	 */
	public static void showSoftInput(EditText editText) {
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		showSoftInput(editText, true);
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param editText
	 */
	public static void hideSoftInput(EditText editText) {
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		showSoftInput(editText, false);
	}

	/**
	 * 显示或者隐藏键盘
	 * 
	 * @param context
	 * @param view
	 * @param isShow
	 */
	@Deprecated
	public static void showSoftInput(Context context, View view, boolean isShow) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (isShow) {
			imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
		} else {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 判断在前台还是后台
	 * 
	 * @return
	 */
	public static boolean isBackground() {
		ActivityManager activityManager = (ActivityManager) Bigapple
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(Bigapple.getApplicationContext()
					.getPackageName())) {
				/*
				 * BACKGROUND=400 EMPTY=500 FOREGROUND=100 GONE=1000
				 * PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
				 */
				LogUtils.i("此appimportace =" + appProcess.importance
						+ ",context.getClass().getName()="
						+ Bigapple.getApplicationContext().getClass().getName());

				if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					LogUtils.i("处于后台" + appProcess.processName);
					return true;
				} else {
					LogUtils.i("处于前台" + appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 判断在前台还是后台
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				/*
				 * BACKGROUND=400 EMPTY=500 FOREGROUND=100 GONE=1000
				 * PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
				 */
				LogUtils.i("此appimportace =" + appProcess.importance
						+ ",context.getClass().getName()="
						+ context.getClass().getName());

				if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					LogUtils.i("处于后台" + appProcess.processName);
					return true;
				} else {
					LogUtils.i("处于前台" + appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

}
