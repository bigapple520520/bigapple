package com.xuan.bigapple.demo.utils;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.xuan.bigapple.R;
import com.xuan.bigapple.lib.ioc.InjectView;
import com.xuan.bigapple.lib.ioc.app.BPActivity;
import com.xuan.bigapple.lib.utils.ToastUtils;
import com.xuan.bigapple.lib.utils.textviewhtml.span.SpannableStringUtils;

/**
 * TextView文本处理
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-8-15 上午11:45:16 $
 */
public class TextViewHtmlDemoActivity extends BPActivity {
	@InjectView(R.id.text1)
	private TextView text1;

	@InjectView(R.id.text2)
	private TextView text2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_utils_textviewhtml_main);

		text1.setMovementMethod(LinkMovementMethod.getInstance());
		text1.setText("好多哈偶的好多哈偶的好多哈偶的好多哈偶的好多哈偶的好多哈偶的好多哈偶的");
		text1.append(SpannableStringUtils.getOnClickSpan("点我啊",
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ToastUtils.displayTextShort(
								TextViewHtmlDemoActivity.this, "111111");
					}
				}));
	}

}
