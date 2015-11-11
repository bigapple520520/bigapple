package com.xuan.bigapple;

import android.app.Application;

import com.xuan.bigapple.lib.Bigapple;
import com.xuan.bigapple.lib.db.DBHelper;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 程序入口处
 * 
 * @author xuan
 */
public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		Bigapple.init(this);

		// 设置日志TAG
		LogUtils.TAG = "BigappleDemo";
		DBHelper.init(1, "bigapple", this);
	}

}
