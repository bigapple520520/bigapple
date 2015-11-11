package com.xuan.bigapple.lib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 需要使用Bigapple框架需要在Application实例初始化的时候调用Bigapple.init(this);方法初始化
 * 
 * @author xuan
 */
public abstract class Bigapple {
	public static Context application;

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if (null == context) {
			LogUtils.e("Bigapple can not init. Cause context is null.");
			return;
		}

		if (application instanceof Activity) {
			LogUtils.d("Bigapple init by Activity.");
			application = context.getApplicationContext();
		} else if (context instanceof Application) {
			LogUtils.d("Bigapple init by Application.");
			application = context;
		} else {
			LogUtils.e("Bigapple can not be init. Cause context is wrong type.");
		}
	}

	/**
	 * 获取当前程序实例
	 * 
	 * @return
	 */
	public static Context getApplicationContext() {
		return application;
	}

}
