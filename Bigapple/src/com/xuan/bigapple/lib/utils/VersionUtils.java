package com.xuan.bigapple.lib.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.xuan.bigapple.lib.Bigapple;

/**
 * 获取AndroidManifest中的一些版本信息
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-5-4 下午1:54:56 $
 */
public abstract class VersionUtils {

	/**
	 * 得到版本代码versionCode（配在AndroidManifest.xml中），主版本号，用于升级应用<br>
	 * 例如：android:versionCode="1"
	 * 
	 * @return
	 */
	public static int getVersionCode() {
		int versionCode = -1;
		try {
			PackageInfo packageInfo = Bigapple
					.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(
							Bigapple.getApplicationContext().getPackageName(),
							0);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionCode;
	}

	/**
	 * 得到版本代码versionCode（配在AndroidManifest.xml中），主版本号，用于升级应用<br>
	 * 例如：android:versionCode="1"
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static int getVersionCode(Context context) {
		int versionCode = -1;
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionCode;
	}

	/**
	 * 得到版本代码versionName（配在AndroidManifest.xml中），给用户看的<br>
	 * 例如：android:versionName="1.0"
	 * 
	 * @return
	 */
	public static String getVersionName() {
		String versionName = "";
		try {
			PackageInfo packageInfo = Bigapple
					.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(
							Bigapple.getApplicationContext().getPackageName(),
							0);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 得到版本代码versionName（配在AndroidManifest.xml中），给用户看的<br>
	 * 例如：android:versionName="1.0"
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获得应用程序图标（配在AndroidManifest.xml中）<br>
	 * 例如：android:icon="@drawable/ic_launcher"
	 * 
	 * @return
	 */
	public static int getApplicationIcon() {
		try {
			PackageInfo packageInfo = Bigapple
					.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(
							Bigapple.getApplicationContext().getPackageName(),
							0);
			return packageInfo.applicationInfo.icon;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * 获得应用程序图标（配在AndroidManifest.xml中）<br>
	 * 例如：android:icon="@drawable/ic_launcher"
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static int getApplicationIcon(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.applicationInfo.icon;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return -1;
	}

}
