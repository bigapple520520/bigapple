package com.winupon.andframe.bigapple.bitmap.callback;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.winupon.andframe.bigapple.bitmap.BitmapDisplayConfig;
import com.winupon.andframe.bigapple.bitmap.extend.RoundedDrawable;

/**
 * 默认实现，加载完图片后的回调显示接口
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午6:16:04 $
 */
public class SimpleImageLoadCallBack implements ImageLoadCallBack {
    @Override
    public void loadCompleted(ImageView imageView, Bitmap bitmap, BitmapDisplayConfig config) {
        if (config.getRoundPx() > 0) {
            RoundedDrawable roundedDrawable = new RoundedDrawable(bitmap);
            roundedDrawable.setCornerRadius(config.getRoundPx());
            imageView.setImageDrawable(roundedDrawable);
        }
        else {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void loadFailed(ImageView imageView, BitmapDisplayConfig config) {
        imageView.setImageBitmap(config.getLoadFailedBitmap());
    }

}
