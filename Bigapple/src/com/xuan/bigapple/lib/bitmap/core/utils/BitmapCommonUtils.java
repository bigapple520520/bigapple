package com.xuan.bigapple.lib.bitmap.core.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 通用图片工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-9 下午3:22:58 $
 */
public class BitmapCommonUtils {

	/**
	 * 获取可以使用的缓存目录，如果SD卡存在就获取缓存到SD，如果SD不存在，就使用内置内存<br>
	 * 默认缓存路径：/Android/data/程序包名/cache/。当应用卸载时，缓存文件会被自动清理。
	 * 
	 * @param context
	 * @param dirName
	 *            目录名称
	 * @return
	 */
	public static String getDiskCacheDir(Context context, String dirName) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? context.getExternalCacheDir()
				.getPath() : context.getCacheDir().getPath();

		return cachePath + File.separator + dirName;
	}

	/**
	 * 获取bitmap的字节大小
	 * 
	 * @param bitmap
	 *            图片
	 * @return
	 */
	public static int getBitmapSize(Bitmap bitmap) {
		if (null == bitmap) {
			LogUtils.d("Bitmap is null.");
			return 0;
		}

		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * 获取目录指定的可用空间
	 * 
	 * @param path
	 *            目录
	 * @return
	 */
	public static long getAvailableSpace(File path) {
		try {
			final StatFs stats = new StatFs(path.getPath());
			return (long) stats.getBlockSize()
					* (long) stats.getAvailableBlocks();
		} catch (Exception e) {
			LogUtils.e("获取可用空间异常，原因：" + e.getMessage(), e);
		}
		return -1;
	}

}
