package com.xuan.bigapple.demo.utils;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xuan.bigapple.R;
import com.xuan.bigapple.lib.ioc.app.BPActivity;
import com.xuan.bigapple.lib.utils.DialogUtils;
import com.xuan.bigapple.lib.utils.ToastUtils;

/**
 * 弹出对话框
 * 
 * @author xuan
 */
public class DialogUtilsDemoActivity extends BPActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.demo_utils_dialogs, null);
		setContentView(root);
		// ///////////////////alert
		Button btn1 = new Button(this);
		btn1.setText("alert测试");
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogUtils.alert(DialogUtilsDemoActivity.this, "提示", "怎么办呀",
						"点我就好");
			}
		});
		root.addView(btn1);

		// ////////////////////////comfirm
		Button btn2 = new Button(this);
		btn2.setText("comfirm测试");
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogUtils.comfirm(DialogUtilsDemoActivity.this, "提示",
						"选我还是她？", "我", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								ToastUtils.displayTextShort(
										DialogUtilsDemoActivity.this, "谢谢你选我");
							}
						}, "她", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								ToastUtils.displayTextShort(
										DialogUtilsDemoActivity.this, "你居然选她");
							}
						});
			}
		});
		root.addView(btn2);

		// ////////////////////////select
		Button btn3 = new Button(this);
		btn3.setText("select测试");
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogUtils.select(DialogUtilsDemoActivity.this, "提示", false,
						new String[] { "第1个", "第2个", "第3个", "第4个" },
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0,
									int position) {
								ToastUtils.displayTextShort(
										DialogUtilsDemoActivity.this,
										String.valueOf(position));
							}
						});
			}
		});
		root.addView(btn3);

		// ////////////////////////select2
		Button btn4 = new Button(this);
		btn4.setText("select2测试");
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogUtils.select2(DialogUtilsDemoActivity.this, "提示", false,
						new String[] { "第1个", "第2个", "第3个", "第4个" },
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0,
									int position) {
								ToastUtils.displayTextShort(
										DialogUtilsDemoActivity.this,
										String.valueOf(position));
							}
						});
			}
		});
		root.addView(btn4);
	}

}
