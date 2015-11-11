package com.xuan.bigapple.demo.utils;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xuan.bigapple.R;
import com.xuan.bigapple.lib.ioc.InjectView;
import com.xuan.bigapple.lib.ioc.app.BPActivity;
import com.xuan.bigapple.lib.utils.ToastUtils;
import com.xuan.bigapple.lib.utils.Validators;
import com.xuan.bigapple.lib.utils.pinyin.PinyinUtil;

/**
 * 拼音测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-5-6 下午1:05:46 $
 */
public class PinyinDemoActivity extends BPActivity {
	@InjectView(R.id.editText)
	private EditText editText;

	@InjectView(R.id.button)
	private Button button;

	@InjectView(R.id.textView)
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_utils_pinyin_main);

		StringBuilder sb = new StringBuilder();
		char ni = '你';
		sb.append("测试‘你’\n");
		sb.append("全拼原串：" + PinyinUtil.toPinyin(ni) + "\n");
		sb.append("全拼小写：" + PinyinUtil.toPinyinLower(ni) + "\n");
		sb.append("全拼大写：" + PinyinUtil.toPinyinUpper(ni) + "\n");
		sb.append("首字母原写：" + PinyinUtil.toPinyinF(ni) + "\n");
		sb.append("首字母小写：" + PinyinUtil.toPinyinLowerF(ni) + "\n");
		sb.append("首字母大写：" + PinyinUtil.toPinyinUpperF(ni) + "\n");
		textView.setText(sb.toString());

		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				String hanzi = editText.getText().toString();
				if (Validators.isEmpty(hanzi)) {
					ToastUtils.displayTextShort(PinyinDemoActivity.this,
							"请输入汉字");
					return;
				}

				StringBuilder sb = new StringBuilder();
				sb.append("全拼原串：" + PinyinUtil.toPinyin(hanzi) + "\n");
				sb.append("全拼小写：" + PinyinUtil.toPinyinLower(hanzi) + "\n");
				sb.append("全拼大写：" + PinyinUtil.toPinyinUpper(hanzi) + "\n");
				sb.append("首字母原写：" + PinyinUtil.toPinyinF(hanzi) + "\n");
				sb.append("首字母小写：" + PinyinUtil.toPinyinLowerF(hanzi) + "\n");
				sb.append("首字母大写：" + PinyinUtil.toPinyinUpperF(hanzi) + "\n");
				textView.setText(sb.toString());
			}
		});
	}

}
