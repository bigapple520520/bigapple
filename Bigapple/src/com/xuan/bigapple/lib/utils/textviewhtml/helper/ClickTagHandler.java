package com.xuan.bigapple.lib.utils.textviewhtml.helper;

import org.xml.sax.XMLReader;

import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 点击Tag处理
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-11 上午11:59:09 $
 */
public class ClickTagHandler implements TagHandler {
	public static final String CLICK_TAG = "clicktag";

	private OnClickListener onClickListener;

	private int startIndex = 0;
	private int stopIndex = 0;

	public ClickTagHandler() {
	}

	public ClickTagHandler(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		if (tag.toLowerCase().equals(CLICK_TAG)) {
			if (opening) {
				startLovean(tag, output, xmlReader);
			} else {
				endLovean(tag, output, xmlReader);
			}
		}
	}

	public void startLovean(String tag, Editable output, XMLReader xmlReader) {
		startIndex = output.length();
	}

	public void endLovean(String tag, Editable output, XMLReader xmlReader) {
		stopIndex = output.length();
		output.setSpan(new OnClickSpan(), startIndex, stopIndex,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	/**
	 * 事件处理
	 * 
	 * @author xuan
	 */
	private class OnClickSpan extends ClickableSpan {
		@Override
		public void onClick(View view) {
			if (null != onClickListener) {
				onClickListener.onClick(view);
			}
		}
	}

}
