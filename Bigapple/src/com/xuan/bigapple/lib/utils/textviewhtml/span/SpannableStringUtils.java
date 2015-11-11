package com.xuan.bigapple.lib.utils.textviewhtml.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View.OnClickListener;

import com.xuan.bigapple.lib.utils.textviewhtml.span.SimpleURLSpan.UrlSpanOnClickListener;

/**
 * SpannableString工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-11 上午10:04:14 $
 */
public abstract class SpannableStringUtils {

	/**
	 * 获取特定的文本
	 * 
	 * @param text
	 * @param span
	 * @return
	 */
	public static SpannableString getSpan(String text, CharacterStyle span) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(span, 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 获取点击文本
	 * 
	 * @param text
	 * @param onClickListener
	 * @return
	 */
	public static SpannableString getOnClickSpan(String text,
			OnClickListener onClickListener) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new SimpleClickableSpan(onClickListener), 0,
				text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 获取超连接文本
	 * 
	 * @param text
	 * @param url
	 * @return
	 */
	public static SpannableString getUrlSpan(String text, String url,
			UrlSpanOnClickListener urlSpanOnClickListener) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new SimpleURLSpan(url, urlSpanOnClickListener), 0,
				text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 获取背景颜色文本
	 * 
	 * @param text
	 * @param color
	 * @return
	 */
	public static SpannableString getBgColorSpan(String text, int color) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new BackgroundColorSpan(color), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 获取颜色文本
	 * 
	 * @param text
	 * @param color
	 * @return
	 */
	public static SpannableString getColorSpan(String text, int color) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new ForegroundColorSpan(color), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 获取字体大小文本
	 * 
	 * @param text
	 * @param size
	 * @return
	 */
	public static SpannableString getFontSpan(String text, int size) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new AbsoluteSizeSpan(size), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 获取字体样式，Typeface
	 * 
	 * @param text
	 * @param style
	 * @return
	 */
	public static SpannableString getStyleSpan(String text, int style) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new StyleSpan(style), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 删除线文本
	 * 
	 * @param text
	 * @return
	 */
	public static SpannableString getStrikeSpan(String text) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new StrikethroughSpan(), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 下划线文本
	 * 
	 * @param text
	 * @return
	 */
	public static SpannableString getUnderLineSpan(String text) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new UnderlineSpan(), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 获取图片文本
	 * 
	 * @param context
	 * @param text
	 * @param bitmap
	 * @return
	 */
	public static SpannableString getImageSpan(Context context, String text,
			Bitmap bitmap) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new ImageSpan(context, bitmap), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	public static SpannableString getImageSpan(Context context, String text,
			int resid) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new ImageSpan(context, resid), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	public static SpannableString getImageSpan(Context context, String text,
			Uri uri) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new ImageSpan(context, uri), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

}
