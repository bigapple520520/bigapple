package com.winupon.andframe.bigapple.utils.textviewhtml.helper;

import org.xml.sax.XMLReader;

import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 自定义Tag的实现，tag的名字是lovean
 * 
 * @author xuan
 */
public class LoveAnTagHandler implements TagHandler {

	private final OnClickListener onClickListener;

	private int startIndex = 0;
	private int stopIndex = 0;

	public LoveAnTagHandler(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		if (tag.toLowerCase().equals("lovean")) {
			if (opening) {
				startGame(tag, output, xmlReader);
			} else {
				endGame(tag, output, xmlReader);
			}
		}

	}

	public void startGame(String tag, Editable output, XMLReader xmlReader) {
		startIndex = output.length();
	}

	public void endGame(String tag, Editable output, XMLReader xmlReader) {
		stopIndex = output.length();
		output.setSpan(new LoveAnSpan(), startIndex, stopIndex,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	/**
	 * 事件处理
	 * 
	 * @author xuan
	 */
	private class LoveAnSpan extends ClickableSpan implements OnClickListener {
		@Override
		public void onClick(View view) {
			if (null != onClickListener) {
				onClickListener.onClick(view);
			}
		}
	}

}
