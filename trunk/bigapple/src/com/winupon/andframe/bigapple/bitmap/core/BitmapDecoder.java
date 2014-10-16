package com.winupon.andframe.bigapple.bitmap.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.winupon.andframe.bigapple.utils.BitmapUtils;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 图片解码器，主要从资源中按适当的比例解码出bitmap <br />
 * 会根据设置要求大小进行调整，这很重要可以很好的防止图片的OOM
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午6:50:10 $
 */
public abstract class BitmapDecoder {
    /**
     * 从资源文件中按指定规格加载图片
     * 
     * @param res
     *            资源
     * @param resId
     *            资源图片id
     * @param reqWidth
     *            要求最大宽
     * @param reqHeight
     *            要求最大高
     * @param config
     *            图片质量配置
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight,
            Bitmap.Config config) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置后只会加载图片参数信息，不会加载图片本身
        options.inPurgeable = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        if (null != config) {
            options.inPreferredConfig = config;
        }

        try {
            return BitmapFactory.decodeResource(res, resId, options);
        }
        catch (OutOfMemoryError e) {
            LogUtils.e("图片内存异常错误，原因：" + e.getMessage(), e);
        }

        return null;
    }

    /**
     * 从文件中加载出图片
     * 
     * @param filename
     *            图片地址
     * @param reqWidth
     *            要求最大宽
     * @param reqHeight
     *            要求最大高
     * @param config
     *            图片质量配置
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight, Bitmap.Config config) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFile(filename, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        if (config != null) {
            options.inPreferredConfig = config;
        }
        try {
            return BitmapFactory.decodeFile(filename, options);
        }
        catch (OutOfMemoryError e) {
            LogUtils.e("图片内存异常错误，原因：" + e.getMessage(), e);
        }

        return null;
    }

    /**
     * 从文件描述中加载出图片
     * 
     * @param fileDescriptor
     *            文件描述
     * @param reqWidth
     *            要求最大宽
     * @param reqHeight
     *            要求最大高
     * @param config
     *            图片质量配置
     * @return
     */
    public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight,
            Bitmap.Config config) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        if (config != null) {
            options.inPreferredConfig = config;
        }
        try {
            return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        }
        catch (OutOfMemoryError e) {
            LogUtils.e("图片内存异常错误，原因：" + e.getMessage(), e);
        }

        return null;
    }

    /**
     * 从图片数据中加载出图片
     * 
     * @param data
     *            图片数据
     * @param reqWidth
     *            要求最大宽
     * @param reqHeight
     *            要求最大高
     * @param config
     *            图片质量配置
     * @return
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight, Bitmap.Config config) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        if (config != null) {
            options.inPreferredConfig = config;
        }
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        }
        catch (OutOfMemoryError e) {
            LogUtils.e("图片内存异常错误，原因：" + e.getMessage(), e);
        }

        return null;
    }

    /**
     * 根据指定规格，计算图片缩放比例
     * 
     * @param options
     *            图片质量参数
     * @param reqWidth
     *            要求最大宽
     * @param reqHeight
     *            要求最大高
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            }
            else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }

        return inSampleSize;
    }

}
