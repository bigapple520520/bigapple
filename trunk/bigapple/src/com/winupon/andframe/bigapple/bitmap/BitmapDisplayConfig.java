package com.winupon.andframe.bigapple.bitmap;

import android.content.Context;
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
	private static final Bitmap TRANSPARENT_BITMAP = Bitmap.createBitmap(50,
			50, Bitmap.Config.ALPHA_8);

<<<<<<< HEAD
    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;// 2.3之后默认参数

    private boolean showOriginal = false;// 是否显示原图
    private int bitmapMaxWidth = 900;// showOriginal=false时才有效
    private int bitmapMaxHeight = 900;// showOriginal=false时才有效
=======
	private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;// 2.3之后默认参数

	/**
	 * 是否显示原图,注意：图片较大叫多时，千万设置false，并控制好下面的最大宽高，不然还是不能避免OOM
	 */
	private boolean showOriginal = false;
	private int bitmapMaxWidth = 900;// showOriginal=false时才有效
	private int bitmapMaxHeight = 900;// showOriginal=false时才有效

	private Animation animation;
>>>>>>> 61bb41709ae045e5229902c7dbe803ce270be42b

	private Bitmap loadingBitmap = TRANSPARENT_BITMAP;
	private Bitmap loadFailedBitmap;

	private ImageLoadCallBack imageLoadCallBack = new SimpleImageLoadCallBack();
	private DownloaderCallBack downloaderCallBack = null;

<<<<<<< HEAD
    private ImageLoadCallBack imageLoadCallBack = new SimpleImageLoadCallBack();

    @Deprecated
    public BitmapDisplayConfig(Context context) {
        // 废弃，效果等于那个无参够着方法
    }

    public BitmapDisplayConfig() {
    }

    public int getBitmapMaxWidth() {
        return bitmapMaxWidth;
    }
=======
	@Deprecated
	public BitmapDisplayConfig(Context context) {
		// 废弃，效果等于那个无参够着方法
	}

	public BitmapDisplayConfig() {
	}

	public int getBitmapMaxWidth() {
		return bitmapMaxWidth;
	}

	public void setBitmapMaxWidth(int bitmapMaxWidth) {
		this.bitmapMaxWidth = bitmapMaxWidth;
	}
>>>>>>> 61bb41709ae045e5229902c7dbe803ce270be42b

	public int getBitmapMaxHeight() {
		return bitmapMaxHeight;
	}

<<<<<<< HEAD
    public int getBitmapMaxHeight() {
        return bitmapMaxHeight;
    }
=======
	public void setBitmapMaxHeight(int bitmapMaxHeight) {
		this.bitmapMaxHeight = bitmapMaxHeight;
	}
>>>>>>> 61bb41709ae045e5229902c7dbe803ce270be42b

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

<<<<<<< HEAD
    public ImageLoadCallBack getImageLoadCallBack() {
        return imageLoadCallBack;
    }
=======
	public void setImageLoadCallBack(ImageLoadCallBack imageLoadCallBack) {
		this.imageLoadCallBack = imageLoadCallBack;
	}
>>>>>>> 61bb41709ae045e5229902c7dbe803ce270be42b

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

	@Override
	public String toString() {
		return isShowOriginal() ? "" : "-" + getBitmapMaxWidth() + "-"
				+ getBitmapMaxHeight();
	}

}
