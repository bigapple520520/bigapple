package com.xuan.bigapple.lib.utils.textviewhtml.span;

import android.text.style.URLSpan;
import android.view.View;

/**
 * 简单的超连接点击事件实现
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-11 上午11:38:53 $
 */
public class SimpleURLSpan extends URLSpan {
	private final UrlSpanOnClickListener urlSpanOnClickListener;

	public SimpleURLSpan(String url,
			UrlSpanOnClickListener urlSpanOnClickListener) {
		super(url);
		this.urlSpanOnClickListener = urlSpanOnClickListener;
	}

	@Override
	public void onClick(View widget) {
		if (null != urlSpanOnClickListener) {
			urlSpanOnClickListener.onClick(widget, getURL());
		} else {
			super.onClick(widget);
		}
	}

	/**
	 * 点击事件
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-11-11 上午11:33:49 $
	 */
	public interface UrlSpanOnClickListener {
		public void onClick(View widget, String url);
	}

}
