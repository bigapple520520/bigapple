package com.xuan.bigapple.lib.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.xuan.bigapple.lib.Bigapple;

/**
 * 获取设备信息工具类
 * 
 * @author xuan
 */
public abstract class DeviceUtils {

	/**
	 * 获取唯一设备号
	 * 
	 * @return
	 */
	public static String getDeviceId() {
		TelephonyManager TelephonyMgr = (TelephonyManager) Bigapple
				.getApplicationContext().getSystemService(
						Context.TELEPHONY_SERVICE);
		return TelephonyMgr.getDeviceId();
	}

	/**
	 * 获取唯一设备号
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static String getDeviceId(Context context) {
		TelephonyManager TelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return TelephonyMgr.getDeviceId();
	}

}
