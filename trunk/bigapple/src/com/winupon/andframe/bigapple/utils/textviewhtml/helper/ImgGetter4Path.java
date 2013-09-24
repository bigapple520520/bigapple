package com.winupon.andframe.bigapple.utils.textviewhtml.helper;

import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;

/**
 * 处理图片时，加载本地的图片
 * 
 * @author xuan
 */
public class ImgGetter4Path implements ImageGetter {
	@Override
	public Drawable getDrawable(String source) {
		Drawable drawable = null;
		drawable = Drawable.createFromPath(source); // 显示本地图片
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		return drawable;
	}

}
