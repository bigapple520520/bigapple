package com.xuan.bigapple.lib.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import com.xuan.bigapple.lib.io.IOUtils;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 截屏工具
 * 
 * @author xuan
 */
public abstract class ScreenshotUtils {

	/**
	 * View保存成图片
	 * 
	 * @param view
	 * @param saveFileName
	 *            截屏后文件保存路劲
	 * @return
	 */
	public static Bitmap shotView(View view, String saveFileName) {
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache();

		// 保存到指定文件
		if (!Validators.isEmpty(saveFileName)) {
			File file = new File(saveFileName);
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
				fos.flush();
			} catch (Exception e) {
				LogUtils.e(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(fos);
			}
		}

		return bitmap;
	}

	/**
	 * 截屏
	 * 
	 * @param activity
	 * @param saveFileName
	 *            截屏后文件保存路劲
	 * @return
	 */
	public static Bitmap shotScreen(Activity activity, String saveFileName) {
		// 获取屏幕
		View decorview = activity.getWindow().getDecorView();
		return shotView(decorview, saveFileName);
	}

}
