package com.winupon.andframe.bigapple.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.animation.Animation;

import com.winupon.andframe.bigapple.bitmap.callback.ImageLoadCallBack;
import com.winupon.andframe.bigapple.bitmap.callback.SimpleImageLoadCallBack;

/**
 * 图片展现配置
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午4:15:05 $
 */
public class BitmapDisplayConfig {
    private static final Bitmap TRANSPARENT_BITMAP = Bitmap.createBitmap(50, 50, Bitmap.Config.ALPHA_8);

    private int bitmapMaxWidth = 0;
    private int bitmapMaxHeight = 0;

    private Animation animation;

    private Bitmap loadingBitmap = TRANSPARENT_BITMAP;
    private Bitmap loadFailedBitmap;

    private ImageLoadCallBack imageLoadCallBack;

    private boolean showOriginal = false;

    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_4444;

    private final Context mContext;

    public BitmapDisplayConfig(Context context) {
        mContext = context;
    }

    public int getBitmapMaxWidth() {
        if (bitmapMaxWidth == 0) {// 图片的显示最大尺寸（为屏幕的大小,默认为屏幕宽度的1/2）
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            bitmapMaxWidth = (int) Math.floor(displayMetrics.widthPixels / 2);
            bitmapMaxHeight = bitmapMaxHeight == 0 ? bitmapMaxWidth : bitmapMaxHeight;
        }
        return bitmapMaxWidth;
    }

    public void setBitmapMaxWidth(int bitmapMaxWidth) {
        this.bitmapMaxWidth = bitmapMaxWidth;
    }

    public int getBitmapMaxHeight() {
        if (bitmapMaxHeight == 0) {// 图片的显示最大尺寸（为屏幕的大小,默认为屏幕宽度的1/2）
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            bitmapMaxHeight = (int) Math.floor(displayMetrics.widthPixels / 2);
            bitmapMaxWidth = bitmapMaxWidth == 0 ? bitmapMaxHeight : bitmapMaxWidth;
        }
        return bitmapMaxHeight;
    }

    public void setBitmapMaxHeight(int bitmapMaxHeight) {
        this.bitmapMaxHeight = bitmapMaxHeight;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Bitmap getLoadingBitmap() {
        return loadingBitmap;
    }

    public void setLoadingBitmap(Bitmap loadingBitmap) {
        this.loadingBitmap = loadingBitmap;
    }

    public Bitmap getLoadFailedBitmap() {
        return loadFailedBitmap;
    }

    public void setLoadFailedBitmap(Bitmap loadFailedBitmap) {
        this.loadFailedBitmap = loadFailedBitmap;
    }

    public ImageLoadCallBack getImageLoadCallBack() {
        if (imageLoadCallBack == null) {
            imageLoadCallBack = new SimpleImageLoadCallBack();
        }
        return imageLoadCallBack;
    }

    public void setImageLoadCallBack(ImageLoadCallBack imageLoadCallBack) {
        this.imageLoadCallBack = imageLoadCallBack;
    }

    public boolean isShowOriginal() {
        return showOriginal;
    }

    public void setShowOriginal(boolean showOriginal) {
        this.showOriginal = showOriginal;
    }

    public Bitmap.Config getBitmapConfig() {
        return bitmapConfig;
    }

    public void setBitmapConfig(Bitmap.Config bitmapConfig) {
        this.bitmapConfig = bitmapConfig;
    }

    @Override
    public String toString() {
        return isShowOriginal() ? "" : "-" + getBitmapMaxWidth() + "-" + getBitmapMaxHeight();
    }

}
