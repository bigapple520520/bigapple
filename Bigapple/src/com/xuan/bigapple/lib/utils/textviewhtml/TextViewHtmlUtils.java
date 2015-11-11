package com.xuan.bigapple.lib.utils.textviewhtml;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.xuan.bigapple.lib.utils.textviewhtml.helper.SimpleImgGetter;

/**
 * TextView使用Html格式的工具类
 * 
 * @author xuan
 */
public abstract class TextViewHtmlUtils {

	/**
	 * 设置html文本格式的内容给textView，但是处理不了图像
	 * 
	 * @param textView
	 * @param htmlStr
	 */
	public static void setTextByHtml(TextView textView, String htmlStr) {
		if (null == htmlStr) {
			return;
		}

		Spanned text = Html.fromHtml(htmlStr);
		textView.setText(text);
	}

	/**
	 * 这里的图像和自定义tag要自定义实现
	 * 
	 * @param textView
	 * @param htmlStr
	 * @param imageGetter
	 * @param tagHandler
	 */
	public static void setTextByHtml(TextView textView, String htmlStr,
			Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
		if (null == htmlStr) {
			return;
		}

		Spanned text = Html.fromHtml(htmlStr, imageGetter, tagHandler);
		textView.setText(text);
	}

	/**
	 * 一般不需要自定tagHandler，就设置成null就可以了
	 * 
	 * @param textView
	 * @param htmlStr
	 * @param imageGetter
	 */
	public static void setTextByHtml(TextView textView, String htmlStr,
			Html.ImageGetter imageGetter) {
		TextViewHtmlUtils.setTextByHtml(textView, htmlStr, imageGetter, null);
	}

	/**
	 * 会有图片的处理，图片是从网络上加载
	 * 
	 * @param textView
	 * @param htmlStr
	 */
	public static void setTextAndImgByHtml4Url(TextView textView, String htmlStr) {
		SimpleImgGetter defaultImgGetter = new SimpleImgGetter(
				SimpleImgGetter.FROM_TYPE_URL);
		TextViewHtmlUtils.setTextByHtml(textView, htmlStr, defaultImgGetter,
				null);
	}

	/**
	 * 会有图片的处理，图片是从本地加载
	 * 
	 * @param textView
	 * @param htmlStr
	 */
	public static void setTextAndImgByHtml4Path(TextView textView,
			String htmlStr) {
		SimpleImgGetter defaultImgGetter = new SimpleImgGetter(
				SimpleImgGetter.FROM_TYPE_PATH);
		TextViewHtmlUtils.setTextByHtml(textView, htmlStr, defaultImgGetter,
				null);
	}

	/**
	 * 会有图片处理，图片是从res中获取
	 * 
	 * @param textView
	 * @param htmlStr
	 * @param context
	 */
	public static void setTextAndImgByHtml4Resid(TextView textView,
			String htmlStr, Context context) {
		SimpleImgGetter defaultImgGetter = new SimpleImgGetter(context,
				SimpleImgGetter.FROM_TYPE_RESID);
		TextViewHtmlUtils.setTextByHtml(textView, htmlStr, defaultImgGetter,
				null);
	}

	/**
	 * 会有图片处理，图片是从res中获取，图片大小自己指定哦
	 * 
	 * @param textView
	 * @param htmlStr
	 * @param context
	 */
	public static void setTextAndImgByHtml4ResidBySize(TextView textView,
			String htmlStr, Context context, int width, int height) {
		SimpleImgGetter defaultImgGetter = new SimpleImgGetter(context,
				SimpleImgGetter.FROM_TYPE_RESID);
		defaultImgGetter.setShowOriginalWH(false);
		defaultImgGetter.setWidth(width);
		defaultImgGetter.setHeight(height);
		TextViewHtmlUtils.setTextByHtml(textView, htmlStr, defaultImgGetter,
				null);
	}

}
