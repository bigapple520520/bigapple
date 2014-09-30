package com.winupon.andframe.bigapple.bitmap;

import android.graphics.Bitmap;
import android.view.animation.Animation;

import com.winupon.andframe.bigapple.bitmap.callback.DownloaderCallBack;
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

    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;// 2.3之后默认参数

    /**
     * 是否显示原图,注意：图片较大叫多时，千万设置false，并控制好下面的最大宽高，不然还是不能避免OOM
     */
    private boolean showOriginal = false;

    /**
     * showOriginal=false时才有效，限制最大宽，默认900
     */
    private int bitmapMaxWidth = 900;

    /**
     * showOriginal=false时才有效，限制最大高，默认900
     */
    private int bitmapMaxHeight = 900;

    /**
     * 加载完成后显示动画
     */
    private Animation animation;

    /**
     * 加载中图片
     */
    private Bitmap loadingBitmap = TRANSPARENT_BITMAP;
    private int loadingBitmapResid;

    /**
     * 加载失败图片
     */
    private Bitmap loadFailedBitmap;
    private int loadFailedBitmapResid;

    /**
     * 加载完成后回调
     */
    private ImageLoadCallBack imageLoadCallBack = new SimpleImageLoadCallBack();

    /**
     * 从网络中下载图片时回调，只有从网络上下载才会触发
     */
    private DownloaderCallBack downloaderCallBack = null;

    /**
     * 图片进行圆角处理，默认不设置值不进行处理
     */
    private float roundPx = 0;

    public int getBitmapMaxWidth() {
        return bitmapMaxWidth;
    }

    public void setBitmapMaxWidth(int bitmapMaxWidth) {
        this.bitmapMaxWidth = bitmapMaxWidth;
    }

    public int getBitmapMaxHeight() {
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

    public DownloaderCallBack getDownloaderCallBack() {
        return downloaderCallBack;
    }

    public void setDownloaderCallBack(DownloaderCallBack downloaderCallBack) {
        this.downloaderCallBack = downloaderCallBack;
    }

    public float getRoundPx() {
        return roundPx;
    }

    public void setRoundPx(float roundPx) {
        this.roundPx = roundPx;
    }

    public int getLoadingBitmapResid() {
        return loadingBitmapResid;
    }

    public void setLoadingBitmapResid(int loadingBitmapResid) {
        this.loadingBitmapResid = loadingBitmapResid;
    }

    public int getLoadFailedBitmapResid() {
        return loadFailedBitmapResid;
    }

    public void setLoadFailedBitmapResid(int loadFailedBitmapResid) {
        this.loadFailedBitmapResid = loadFailedBitmapResid;
    }

    @Override
    public String toString() {
        String temp = isShowOriginal() ? "" : "-" + getBitmapMaxWidth() + "-" + getBitmapMaxHeight();
        return roundPx > 0 ? temp + "-" + roundPx : temp;
    }

}
