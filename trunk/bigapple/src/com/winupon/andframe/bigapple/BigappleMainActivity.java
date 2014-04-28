package com.winupon.andframe.bigapple;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.winupon.andframe.bigapple.ioc.AnActivity;
import com.winupon.andframe.bigapple.ioc.InjectView;
import com.winupon.andframe.bigapple.zzdemo.bitmap.BitmapDemoActivity;
import com.winupon.andframe.bigapple.zzdemo.db.DbDemoActivity;
import com.winupon.andframe.bigapple.zzdemo.http2.UrlHttpClientDemoActivity;
import com.winupon.andframe.bigapple.zzdemo.ioc.IocDemoActivity;

public class BigappleMainActivity extends AnActivity {

	@InjectView(R.id.content)
	private LinearLayout content;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bigapple_main);

		addButton("bitmap部分模块测试", BitmapDemoActivity.class);
		addButton("http2部分模块测试", UrlHttpClientDemoActivity.class);
		addButton("ioc部分模块测试", IocDemoActivity.class);
		addButton("db部分模块测试", DbDemoActivity.class);
	}

	private void addButton(String text, final Class<?> clazz) {
		Button button = new Button(BigappleMainActivity.this);
		button.setText(text);
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(BigappleMainActivity.this, clazz);
				startActivity(intent);
			}
		});
		content.addView(button);
	}

}
