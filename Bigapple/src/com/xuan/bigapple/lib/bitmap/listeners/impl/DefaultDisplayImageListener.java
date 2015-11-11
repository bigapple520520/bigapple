package com.xuan.bigapple.lib.bitmap.listeners.impl;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xuan.bigapple.lib.bitmap.BitmapDisplayConfig;
import com.xuan.bigapple.lib.bitmap.extend.RoundedDrawable;
import com.xuan.bigapple.lib.bitmap.listeners.DisplayImageListener;

/**
 * 默认实现，加载完图片后的回调显示接口
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午6:16:04 $
 */
public class DefaultDisplayImageListener implements DisplayImageListener {
	@Override
	public void loadCompleted(ImageView imageView, Bitmap bitmap,
			BitmapDisplayConfig config) {
		if (config.getRoundPx() > 0) {
			RoundedDrawable roundedDrawable = new RoundedDrawable(bitmap);
			roundedDrawable.setCornerRadius(config.getRoundPx());
			imageView.setImageDrawable(roundedDrawable);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	@Override
	public void loadFailed(ImageView imageView, BitmapDisplayConfig config) {
		imageView.setImageBitmap(config.getLoadFailedBitmap());
	}

}
