package com.xuan.bigapple.lib.bitmap.extend;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.widget.ImageView.ScaleType;

/**
 * 圆角资源类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-9-22 下午3:24:47 $
 */
public class RoundedDrawable extends Drawable {
	public static final String TAG = "RoundedDrawable";
	public static final int DEFAULT_BORDER_COLOR = Color.BLACK;

	private final RectF mBounds = new RectF();
	private final RectF mDrawableRect = new RectF();
	private final RectF mBitmapRect = new RectF();
	private final BitmapShader mBitmapShader;
	private final Paint mBitmapPaint;
	private final int mBitmapWidth;
	private final int mBitmapHeight;
	private final RectF mBorderRect = new RectF();
	private final Paint mBorderPaint;
	private final Matrix mShaderMatrix = new Matrix();

	private float mCornerRadius = 0;
	private boolean mOval = false;
	private float mBorderWidth = 0;
	private ColorStateList mBorderColor = ColorStateList
			.valueOf(DEFAULT_BORDER_COLOR);
	private ScaleType mScaleType = ScaleType.FIT_CENTER;

	public RoundedDrawable(Bitmap bitmap) {
		mBitmapWidth = bitmap.getWidth();
		mBitmapHeight = bitmap.getHeight();
		mBitmapRect.set(0, 0, mBitmapWidth, mBitmapHeight);

		mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);
		mBitmapShader.setLocalMatrix(mShaderMatrix);

		mBitmapPaint = new Paint();
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

		mBorderPaint = new Paint();
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor.getColorForState(getState(),
				DEFAULT_BORDER_COLOR));
		mBorderPaint.setStrokeWidth(mBorderWidth);
	}

	@Override
	public boolean isStateful() {
		return mBorderColor.isStateful();
	}

	@Override
	protected boolean onStateChange(int[] state) {
		int newColor = mBorderColor.getColorForState(state, 0);
		if (mBorderPaint.getColor() != newColor) {
			mBorderPaint.setColor(newColor);
			return true;
		} else {
			return super.onStateChange(state);
		}
	}

	private void updateShaderMatrix() {
		float scale;
		float dx;
		float dy;

		switch (mScaleType) {
		case CENTER:
			mBorderRect.set(mBounds);
			mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);

			mShaderMatrix.set(null);
			mShaderMatrix
					.setTranslate(
							(int) ((mBorderRect.width() - mBitmapWidth) * 0.5f + 0.5f),
							(int) ((mBorderRect.height() - mBitmapHeight) * 0.5f + 0.5f));
			break;
		case CENTER_CROP:
			mBorderRect.set(mBounds);
			mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);

			mShaderMatrix.set(null);

			dx = 0;
			dy = 0;
			if (mBitmapWidth * mBorderRect.height() > mBorderRect.width()
					* mBitmapHeight) {
				scale = mBorderRect.height() / mBitmapHeight;
				dx = (mBorderRect.width() - mBitmapWidth * scale) * 0.5f;
			} else {
				scale = mBorderRect.width() / mBitmapWidth;
				dy = (mBorderRect.height() - mBitmapHeight * scale) * 0.5f;
			}

			mShaderMatrix.setScale(scale, scale);
			mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
					(int) (dy + 0.5f) + mBorderWidth);
			break;
		case CENTER_INSIDE:
			mShaderMatrix.set(null);

			if (mBitmapWidth <= mBounds.width()
					&& mBitmapHeight <= mBounds.height()) {
				scale = 1.0f;
			} else {
				scale = Math.min(mBounds.width() / mBitmapWidth,
						mBounds.height() / mBitmapHeight);
			}

			dx = (int) ((mBounds.width() - mBitmapWidth * scale) * 0.5f + 0.5f);
			dy = (int) ((mBounds.height() - mBitmapHeight * scale) * 0.5f + 0.5f);
			mShaderMatrix.setScale(scale, scale);
			mShaderMatrix.postTranslate(dx, dy);

			mBorderRect.set(mBitmapRect);
			mShaderMatrix.mapRect(mBorderRect);
			mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
			mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect,
					Matrix.ScaleToFit.FILL);
			break;
		default:
		case FIT_CENTER:
			mBorderRect.set(mBitmapRect);
			mShaderMatrix.setRectToRect(mBitmapRect, mBounds,
					Matrix.ScaleToFit.CENTER);
			mShaderMatrix.mapRect(mBorderRect);
			mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
			mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect,
					Matrix.ScaleToFit.FILL);
			break;
		case FIT_END:
			mBorderRect.set(mBitmapRect);
			mShaderMatrix.setRectToRect(mBitmapRect, mBounds,
					Matrix.ScaleToFit.END);
			mShaderMatrix.mapRect(mBorderRect);
			mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
			mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect,
					Matrix.ScaleToFit.FILL);
			break;
		case FIT_START:
			mBorderRect.set(mBitmapRect);
			mShaderMatrix.setRectToRect(mBitmapRect, mBounds,
					Matrix.ScaleToFit.START);
			mShaderMatrix.mapRect(mBorderRect);
			mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
			mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect,
					Matrix.ScaleToFit.FILL);
			break;
		case FIT_XY:
			mBorderRect.set(mBounds);
			mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
			mShaderMatrix.set(null);
			mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect,
					Matrix.ScaleToFit.FILL);
			break;
		}

		mDrawableRect.set(mBorderRect);
		mBitmapShader.setLocalMatrix(mShaderMatrix);
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mBounds.set(bounds);
		updateShaderMatrix();
	}

	@Override
	public void draw(Canvas canvas) {
		if (mOval) {
			if (mBorderWidth > 0) {
				canvas.drawOval(mDrawableRect, mBitmapPaint);
				canvas.drawOval(mBorderRect, mBorderPaint);
			} else {
				canvas.drawOval(mDrawableRect, mBitmapPaint);
			}
		} else {
			if (mBorderWidth > 0) {
				canvas.drawRoundRect(mDrawableRect, Math.max(mCornerRadius, 0),
						Math.max(mCornerRadius, 0), mBitmapPaint);
				canvas.drawRoundRect(mBorderRect, mCornerRadius, mCornerRadius,
						mBorderPaint);
			} else {
				canvas.drawRoundRect(mDrawableRect, mCornerRadius,
						mCornerRadius, mBitmapPaint);
			}
		}
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
		mBitmapPaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mBitmapPaint.setColorFilter(cf);
		invalidateSelf();
	}

	@Override
	public void setDither(boolean dither) {
		mBitmapPaint.setDither(dither);
		invalidateSelf();
	}

	@Override
	public void setFilterBitmap(boolean filter) {
		mBitmapPaint.setFilterBitmap(filter);
		invalidateSelf();
	}

	@Override
	public int getIntrinsicWidth() {
		return mBitmapWidth;
	}

	@Override
	public int getIntrinsicHeight() {
		return mBitmapHeight;
	}

	/**
	 * 获取圆角弧度
	 * 
	 * @return
	 */
	public float getCornerRadius() {
		return mCornerRadius;
	}

	/**
	 * 设置圆角弧度
	 * 
	 * @param radius
	 * @return
	 */
	public RoundedDrawable setCornerRadius(float radius) {
		mCornerRadius = radius;
		return this;
	}

	/**
	 * 获取边界宽度
	 * 
	 * @return
	 */
	public float getBorderWidth() {
		return mBorderWidth;
	}

	/**
	 * 这是边界宽度，单位px
	 * 
	 * @param width
	 * @return
	 */
	public RoundedDrawable setBorderWidth(float width) {
		mBorderWidth = width;
		mBorderPaint.setStrokeWidth(mBorderWidth);
		return this;
	}

	/**
	 * 获取默认边界颜色
	 * 
	 * @return
	 */
	public int getBorderColor() {
		return mBorderColor.getDefaultColor();
	}

	/**
	 * 设置边界颜色
	 * 
	 * @param color
	 * @return
	 */
	public RoundedDrawable setBorderColor(int color) {
		return setBorderColor(ColorStateList.valueOf(color));
	}

	/**
	 * 获取边界颜色列表
	 * 
	 * @return
	 */
	public ColorStateList getBorderColors() {
		return mBorderColor;
	}

	/**
	 * 设置边界颜色列表
	 * 
	 * @param colors
	 * @return
	 */
	public RoundedDrawable setBorderColor(ColorStateList colors) {
		mBorderColor = colors != null ? colors : ColorStateList.valueOf(0);
		mBorderPaint.setColor(mBorderColor.getColorForState(getState(),
				DEFAULT_BORDER_COLOR));
		return this;
	}

	/**
	 * 获取是否绘制成椭圆
	 * 
	 * @return
	 */
	public boolean isOval() {
		return mOval;
	}

	/**
	 * 设置资源是否绘制成椭圆
	 * 
	 * @param oval
	 * @return
	 */
	public RoundedDrawable setOval(boolean oval) {
		mOval = oval;
		return this;
	}

	/**
	 * 获取ScaleType类型
	 * 
	 * @return
	 */
	public ScaleType getScaleType() {
		return mScaleType;
	}

	/**
	 * 设置资源的ScaleType类型
	 * 
	 * @param scaleType
	 * @return
	 */
	public RoundedDrawable setScaleType(ScaleType scaleType) {
		if (null == scaleType) {
			scaleType = ScaleType.FIT_CENTER;
		}
		if (mScaleType != scaleType) {
			mScaleType = scaleType;
			updateShaderMatrix();
		}
		return this;
	}

	/**
	 * 把本资源转换成bitmap对象
	 * 
	 * @return
	 */
	public Bitmap toBitmap() {
		return RoundedDrawable.drawableToBitmap(this);
	}

	// ///////////////////////////////////////静态工具类方法//////////////////////////////////////////////////////
	/**
	 * 根据图片获取圆角资源
	 * 
	 * @param bitmap
	 * @return
	 */
	public static RoundedDrawable fromBitmap(Bitmap bitmap) {
		if (null != bitmap) {
			return new RoundedDrawable(bitmap);
		} else {
			return null;
		}
	}

	/**
	 * 普通资源装换成圆角资源
	 * 
	 * @param drawable
	 * @return
	 */
	public static Drawable fromDrawable(Drawable drawable) {
		if (null != drawable) {
			if (drawable instanceof RoundedDrawable) {
				return drawable;// 如果原本就是圆角类型，直接返回
			} else if (drawable instanceof LayerDrawable) {
				LayerDrawable ld = (LayerDrawable) drawable;
				int num = ld.getNumberOfLayers();

				// 如果是层叠资源，循环每张图片进行处理
				for (int i = 0; i < num; i++) {
					Drawable d = ld.getDrawable(i);
					ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d));
				}
				return ld;
			}

			// 从资源中获取图片
			Bitmap bm = drawableToBitmap(drawable);
			if (null != bm) {
				return new RoundedDrawable(bm);
			} else {
				Log.w(TAG, "Failed to create bitmap from drawable!");
			}
		}
		return drawable;
	}

	/**
	 * 资源转换成图片
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap;
		int width = Math.max(drawable.getIntrinsicWidth(), 1);
		int height = Math.max(drawable.getIntrinsicHeight(), 1);
		try {
			bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		}

		return bitmap;
	}

}
