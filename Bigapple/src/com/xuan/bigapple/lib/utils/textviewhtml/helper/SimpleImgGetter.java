package com.xuan.bigapple.lib.utils.textviewhtml.helper;

import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;

import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 系统默认实现，img标签的解析
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-10-18 下午2:17:24 $
 */
public class SimpleImgGetter implements ImageGetter {
	public static final int FROM_TYPE_PATH = 1;// 本地图片地址
	public static final int FROM_TYPE_URL = 2;// 网络图片地址
	public static final int FROM_TYPE_RESID = 3;// 本地图片resid

	private Context context;
	private int fromType = FROM_TYPE_RESID;

	private boolean showOriginalWH = true;// 是否显示原图的高和宽
	private int width;
	private int height;

	private CreateDrawableCallback createDrawableCallback;

	public SimpleImgGetter() {
	}

	public SimpleImgGetter(int fromType) {
		this.fromType = fromType;
	}

	public SimpleImgGetter(Context context) {
		this.context = context;
	}

	public SimpleImgGetter(Context context, int fromType) {
		this.context = context;
		this.fromType = fromType;
	}

	@Override
	public Drawable getDrawable(String source) {
		Drawable drawable = null;

		try {
			if (null != createDrawableCallback) {
				drawable = createDrawableCallback.getDrawable(source);
			} else {
				switch (fromType) {
				case FROM_TYPE_PATH:
					drawable = Drawable.createFromPath(source);
					break;
				case FROM_TYPE_URL:
					drawable = Drawable.createFromStream(
							new URL(source).openStream(), "");
					break;
				case FROM_TYPE_RESID:
					drawable = context.getResources().getDrawable(
							Integer.valueOf(source));
					break;
				}

				if (showOriginalWH) {
					width = drawable.getIntrinsicWidth();
					height = drawable.getIntrinsicHeight();
				}
				drawable.setBounds(0, 0, width, height);
			}
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
		}

		return drawable;
	}

	public int getFromType() {
		return fromType;
	}

	public void setFromType(int fromType) {
		this.fromType = fromType;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isShowOriginalWH() {
		return showOriginalWH;
	}

	public void setShowOriginalWH(boolean showOriginalWH) {
		this.showOriginalWH = showOriginalWH;
	}

	public void setCreateDrawable(CreateDrawableCallback createDrawableCallback) {
		this.createDrawableCallback = createDrawableCallback;
	}

	/**
	 * 自定义处理图片资源
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-10-18 下午2:45:45 $
	 */
	public interface CreateDrawableCallback {
		public Drawable getDrawable(String source);
	}

}
