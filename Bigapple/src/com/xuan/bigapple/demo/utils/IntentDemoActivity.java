package com.xuan.bigapple.demo.utils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xuan.bigapple.lib.ioc.app.BPActivity;
import com.xuan.bigapple.lib.utils.IntentUtils;

/**
 * 一般调用系统自带功能调用
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-12-8 下午2:04:23 $
 */
public class IntentDemoActivity extends BPActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout root = new LinearLayout(this);
		root.setOrientation(LinearLayout.VERTICAL);
		setContentView(root);

		// 选择文件器
		Button button1 = new Button(this);
		button1.setText("选择文件器");
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				IntentUtils.showFileChooser(IntentDemoActivity.this, 1);
			}
		});
		root.addView(button1);
	}

}
