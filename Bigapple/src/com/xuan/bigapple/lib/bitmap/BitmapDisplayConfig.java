package com.xuan.bigapple.lib.bitmap;

import android.graphics.Bitmap;
import android.view.animation.Animation;

import com.xuan.bigapple.lib.bitmap.listeners.DisplayImageListener;
import com.xuan.bigapple.lib.bitmap.listeners.DownloaderProcessListener;
import com.xuan.bigapple.lib.bitmap.listeners.impl.DefaultDisplayImageListener;

/**
 * 图片展现配置
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午4:15:05 $
 */
public class BitmapDisplayConfig {
	/** 图片质量：2.3之后默认参数 */
	private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;

	/** 是否显示原图,注意：图片较大较多时，千万设置false，并控制好下面的最大宽高，不然还是不能避免OOM */
	private boolean showOriginal = false;
	/** showOriginal=false时才有效，限制最大宽，默认900 */
	private int bitmapMaxWidth = 900;
	/** showOriginal=false时才有效，限制最大高，默认900 */
	private int bitmapMaxHeight = 900;

	/** 加载完成后显示动画 */
	private Animation animation;

	/** 加载中图片 */
	private Bitmap loadingBitmap = Bitmap.createBitmap(50, 50,
			Bitmap.Config.ARGB_8888);
	/** 加载失败图片 */
	private Bitmap loadFailedBitmap;

	/** 加载完成后回调 */
	private DisplayImageListener displayImageListener;
	/** 从网络中下载图片时回调，只有从网络上下载才会触发 */
	private DownloaderProcessListener downloaderCallBack;

	/** 图片进行圆角处理，默认不设置值不进行处理 */
	private float roundPx = 0;

	/** 配置的tag值 */
	private Object tag;

	/**
	 * 获取图片大于bitmapMaxWidth的最小宽
	 * 
	 * @return
	 */
	public int getBitmapMaxWidth() {
		return bitmapMaxWidth;
	}

	/**
	 * 设置图片大于bitmapMaxWidth的最小宽
	 * 
	 * @param bitmapMaxWidth
	 */
	public void setBitmapMaxWidth(int bitmapMaxWidth) {
		this.bitmapMaxWidth = bitmapMaxWidth;
	}

	/**
	 * 获取图片大于bitmapMaxHeight的最小高
	 * 
	 * @return
	 */
	public int getBitmapMaxHeight() {
		return bitmapMaxHeight;
	}

	/**
	 * 设置图片大于bitmapMaxHeight的最小高
	 * 
	 * @param bitmapMaxHeight
	 */
	public void setBitmapMaxHeight(int bitmapMaxHeight) {
		this.bitmapMaxHeight = bitmapMaxHeight;
	}

	/**
	 * 获取显示动画
	 * 
	 * @return
	 */
	public Animation getAnimation() {
		return animation;
	}

	/**
	 * 设置显示动画
	 * 
	 * @param animation
	 */
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	/**
	 * 获取加载中图片
	 * 
	 * @return
	 */
	public Bitmap getLoadingBitmap() {
		return loadingBitmap;
	}

	/**
	 * 设置加载中图片
	 * 
	 * @param loadingBitmap
	 */
	public void setLoadingBitmap(Bitmap loadingBitmap) {
		this.loadingBitmap = loadingBitmap;
	}

	/**
	 * 获取加载失败图片
	 * 
	 * @return
	 */
	public Bitmap getLoadFailedBitmap() {
		return loadFailedBitmap;
	}

	/**
	 * 设置加载失败图片
	 * 
	 * @param loadFailedBitmap
	 */
	public void setLoadFailedBitmap(Bitmap loadFailedBitmap) {
		this.loadFailedBitmap = loadFailedBitmap;
	}

	public DisplayImageListener getDisplayImageListener() {
		if (null == displayImageListener) {
			displayImageListener = new DefaultDisplayImageListener();
		}

		return displayImageListener;
	}

	public void setDisplayImageListener(
			DisplayImageListener displayImageListener) {
		this.displayImageListener = displayImageListener;
	}

	/**
	 * 是否原图显示
	 * 
	 * @return
	 */
	public boolean isShowOriginal() {
		return showOriginal;
	}

	/**
	 * 设置是否原图显示
	 * 
	 * @param showOriginal
	 */
	public void setShowOriginal(boolean showOriginal) {
		this.showOriginal = showOriginal;
	}

	/**
	 * 获取图片显示规格
	 * 
	 * @return
	 */
	public Bitmap.Config getBitmapConfig() {
		return bitmapConfig;
	}

	/**
	 * 设置图片显示规格
	 * 
	 * @param bitmapConfig
	 */
	public void setBitmapConfig(Bitmap.Config bitmapConfig) {
		this.bitmapConfig = bitmapConfig;
	}

	/**
	 * 获取图片下载中回调
	 * 
	 * @return
	 */
	public DownloaderProcessListener getDownloaderCallBack() {
		return downloaderCallBack;
	}

	/**
	 * 设置图片下载中回调
	 * 
	 * @param downloaderCallBack
	 */
	public void setDownloaderCallBack(
			DownloaderProcessListener downloaderCallBack) {
		this.downloaderCallBack = downloaderCallBack;
	}

	/**
	 * 获取该配置tag
	 * 
	 * @return
	 */
	public Object getTag() {
		return tag;
	}

	/**
	 * 设置该配置tag
	 * 
	 * @param tag
	 */
	public void setTag(Object tag) {
		this.tag = tag;
	}

	/**
	 * 获取图片圆角
	 * 
	 * @return
	 */
	public float getRoundPx() {
		return roundPx;
	}

	/**
	 * 设置图片圆角
	 * 
	 * @param roundPx
	 */
	public void setRoundPx(float roundPx) {
		this.roundPx = roundPx;
	}

	@Override
	public String toString() {
		String temp = isShowOriginal() ? "" : "-" + getBitmapMaxWidth() + "-"
				+ getBitmapMaxHeight();
		return roundPx > 0 ? temp + "-" + roundPx : temp;
	}

}
