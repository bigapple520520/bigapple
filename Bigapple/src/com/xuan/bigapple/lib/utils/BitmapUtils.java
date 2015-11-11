package com.xuan.bigapple.lib.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;

import com.xuan.bigapple.lib.bitmap.core.utils.BitmapDecoder;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 操作Bitmap常用工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-7-10 下午7:03:59 $
 */
public abstract class BitmapUtils {

	/**
	 * 重新绘制一个bitmap，这个bitmap是原有图片的圆角，圆角是图片宽的一半
	 * 
	 * @param sourceBitmap
	 *            原有图片
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap sourceBitmap) {
		if (null == sourceBitmap) {
			LogUtils.e("SourceBitmap is null.");
			return null;
		}

		return getRoundedCornerBitmap(sourceBitmap, sourceBitmap.getWidth() / 2);
	}

	/**
	 * 重新绘制一个bitmap，这个bitmap是原有图片的圆角
	 * 
	 * @param sourceBitmap
	 *            原有图片
	 * @param roundPx
	 *            圆角幅度
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap sourceBitmap,
			float roundPx) {
		if (null == sourceBitmap) {
			LogUtils.e("SourceBitmap is null.");
			return null;
		}

		try {
			Bitmap roundedBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
					sourceBitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(roundedBitmap);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, sourceBitmap.getWidth(),
					sourceBitmap.getHeight());
			final RectF rectF = new RectF(rect);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(sourceBitmap, rect, rect, paint);

			return roundedBitmap;
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 压缩图片宽高尺寸
	 * 
	 * @param src
	 *            源图片的路径
	 * @param dest
	 *            目标图片的路径
	 * @param minWidth
	 *            限制宽，大于该宽的最小值
	 * @param minHeight
	 *            限制高，大于该宽的最小值
	 * @param degree
	 *            需要旋转的角度，-1表示不操作旋转
	 * @throws IOException
	 */
	public static Bitmap changeOppositeSizeMayDegree(String src, String dest,
			int minWidth, int minHeight, int degree) {
		OutputStream out = null;
		Bitmap bitmap = null;
		Bitmap newBitmap = null;
		try {
			bitmap = BitmapDecoder.decodeSampledBitmapFromFile(src, minWidth,
					minHeight, Config.ARGB_8888);
			if (null == bitmap) {
				LogUtils.e("Change Failed. Cause bitmap from decode is null.");
				return null;
			}

			if (-1 == degree) {
				newBitmap = bitmap;
			} else {
				newBitmap = rotateBitMap(bitmap, degree);// 调整图片角度
			}

			File file = new File(dest);
			createParentDirs(file);
			out = new BufferedOutputStream(new FileOutputStream(file));
			newBitmap.compress(CompressFormat.JPEG, 70, out);// 保存到目的地
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
			return null;
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				// Ignore
			}
		}

		return newBitmap;
	}

	/**
	 * 将bitmap保存到文件中
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean saveBitmapToFile(Bitmap bitmap, String filePath,
			int quality) {
		FileOutputStream out = null;
		try {
			File file = new File(filePath);
			file.createNewFile();
			try {
				out = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				LogUtils.e("File can't find. Cause：" + e.getMessage(), e);
				return false;
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
			return true;
		} catch (Exception e) {
			LogUtils.e("Compress file failed. Cause：" + e.getMessage(), e);
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				// Ignore
			}
		}

		return false;
	}

	/**
	 * 获取指定路径下图片的旋转角度
	 * 
	 * @param filePath
	 *            指定图片路径
	 * @return
	 */
	public static int getBitmapDegree(String filePath) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(filePath);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			LogUtils.e(e.getMessage(), e);
		}
		return degree;
	}

	/**
	 * 获取旋转后的图片
	 * 
	 * @param sourceBitmap
	 *            图片对象
	 * @param degree
	 *            旋转角度
	 * @return
	 */
	public static Bitmap rotateBitMap(Bitmap sourceBitmap, int degree) {
		Bitmap rotateBitmap = null;
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.postRotate(degree);
		try {
			rotateBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
					sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix,
					true);
		} catch (OutOfMemoryError e) {
			LogUtils.e(e.getMessage(), e);
		}

		if (null == rotateBitmap) {
			LogUtils.e("Bitmap can not ratate.So return source bitmap.");
			rotateBitmap = sourceBitmap;
		}

		return rotateBitmap;
	}

	/**
	 * 创建指定文件的父文件夹
	 * 
	 * @param file
	 *            指定文件
	 */
	public static void createParentDirs(File file) {
		File parentPath = file.getParentFile();
		if (!parentPath.exists() || !parentPath.isDirectory()) {
			parentPath.mkdirs();
		}
	}

}
