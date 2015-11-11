package com.xuan.bigapple.lib.utils.textviewhtml.span;

import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 简单的点击块实现
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-11 上午11:48:09 $
 */
public class SimpleClickableSpan extends ClickableSpan {
	private OnClickListener onClickListener;

	public SimpleClickableSpan() {
	}

	public SimpleClickableSpan(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	@Override
	public void onClick(View widget) {
		if (null != onClickListener) {
			onClickListener.onClick(widget);
		}
	}

}
