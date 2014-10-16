/* 
 * @(#)BitmapUtils.java    Created on 2014-7-10
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.winupon.andframe.bigapple.utils.log.LogUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 操作Bitmap常用工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-7-10 下午7:03:59 $
 */
public abstract class BitmapUtils {

    /**
     * 重新绘制一个bitmap，这个bitmap是原有图片的圆角
     * 
     * @param bitmap
     *            原有图片
     * @param roundPx
     *            圆角幅度
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (null == bitmap) {
            return null;
        }
        
        try {
        	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
		} catch (Exception e) {
			LogUtils.e("图片获取圆角异常，原因："+e.getMessage(), e);
		}
        
        return null;
    }
    
    /**
     * 将bitmap保存到文件中
     * 
     * @param filePath
     * @return
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, String filePath, int quality){
    	try {
    		File file = new File(filePath);  
    		file.createNewFile();  
    	    FileOutputStream out = null;  
    	    try {  
    	    	out = new FileOutputStream(file);  
    	    } catch (FileNotFoundException e) {  
    	        LogUtils.e("文件未找到异常，原因："+e.getMessage(), e);
    	        return false;
    	    }  
    	    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
    	    out.flush();
    	    return true;
		} catch (Exception e) {
			LogUtils.e("保存Bitmap到文件异常，原因："+e.getMessage(), e);
		}
    	
    	return false;
    }
    
    //////////////////////////////////////////压缩图片质量部分///////////////////////////////////////
    /**
     * 压缩图片（好像不准的，有待考究）
     * 
     * @param bitmap 被压缩的图片
     * @param reqSize 压缩后大小限制，单位byte
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int reqSize){
    	try {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
            int options = 100;  
    		
            //循环判断如果压缩后图片是否大于reqSize,大于继续压缩       
            while (baos.toByteArray().length > reqSize) {  
            	options -= 10;//每次都减少10 
            	baos.reset();//重置baos即清空baos  
            	bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            return BitmapFactory.decodeStream(isBm);
		} catch (Exception e) {
			LogUtils.e("图片压缩异常，原因："+e.getMessage(), e);
		}
    	
    	return null;
    }
    
    /**
     * 压缩图片（好像不准的，有待考究）
     * 
     * @param fromBitmap
     * @param toBitmap
     * @param reqSize
     * @return
     */
    public static boolean compressBitmap(String fromBitmapPath, String toBitmapPath, int reqSize){
    	try {
    		Bitmap fromBitmap =  BitmapFactory.decodeFile(fromBitmapPath);
    		Bitmap toBitmap = compressBitmap(fromBitmap, reqSize);
    		if(null == toBitmap){
    			return false;
    		}
    		
    		//压缩成功保存到磁盘
    		return BitmapUtils.saveBitmapToFile(toBitmap, toBitmapPath, 100);
		} catch (Exception e) {
			LogUtils.e("图片压缩异常，原因："+e.getMessage(), e);
		}
    	
    	return false;
    }

}
