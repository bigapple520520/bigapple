package com.xuan.bigapple;

import java.io.File;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xuan.bigapple.demo.bitmap.NetBitmapDemoActivity;
import com.xuan.bigapple.demo.db.DbDemoActivity;
import com.xuan.bigapple.demo.http.HttpDemoActivity;
import com.xuan.bigapple.demo.ioc.IocDemoActivity;
import com.xuan.bigapple.demo.utils.DialogUtilsDemoActivity;
import com.xuan.bigapple.demo.utils.IntentDemoActivity;
import com.xuan.bigapple.demo.utils.PinyinDemoActivity;
import com.xuan.bigapple.demo.utils.TextViewHtmlDemoActivity;
import com.xuan.bigapple.lib.ioc.InjectView;
import com.xuan.bigapple.lib.ioc.app.BPActivity;
import com.xuan.bigapple.lib.utils.ContextUtils;
import com.xuan.bigapple.lib.utils.IntentUtils;
import com.xuan.bigapple.lib.utils.ScreenshotUtils;
import com.xuan.bigapple.lib.utils.ToastUtils;
import com.xuan.bigapple.lib.utils.log.LogUtils;
import com.xuan.bigapple.lib.utils.updater.ApkUpdateConfig;
import com.xuan.bigapple.lib.utils.updater.ApkUpdater;

public class BigappleMainActivity extends BPActivity {

	@InjectView(R.id.content)
	private LinearLayout content;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bigapple_main);

		LogUtils.d("日志测试");

		addButton("bitmap部分模块测试", NetBitmapDemoActivity.class);
		addButton("http部分模块测试", HttpDemoActivity.class);
		addButton("ioc部分模块测试", IocDemoActivity.class);
		addButton("db部分模块测试", DbDemoActivity.class);
		addButton("utils之pinyin模块测试", PinyinDemoActivity.class);
		addButton("utils之一键分享测试", new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentUtils.share(
						BigappleMainActivity.this,
						"不好意思我在测试android的一键分享功能",
						Uri.fromFile(new File(ContextUtils.getSdCardPath()
								+ "/xuan/1.jpg")));
			}
		});
		addButton("utils之update模块测试", new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ApkUpdateConfig config = new ApkUpdateConfig();
				config.setProgressListener(new ApkUpdateConfig.ProgressListener() {
					@Override
					public boolean cancel(DialogInterface dialogInterface) {
						ToastUtils.displayTextShort(BigappleMainActivity.this,
								"您已取消下载");
						return false;
					}
				});

				new Thread(new Runnable() {
					@Override
					public void run() {
						ApkUpdater
								.update(BigappleMainActivity.this,
										"http://gdown.baidu.com/data/wisegame/a279c52cd4acedb7/xiangji360_606.apk",
										Environment
												.getExternalStorageDirectory()
												.getPath()
												+ "/xuan1/bigapple-default.apk",
										null, config);
					}
				}).start();
			}
		});
		addButton("utils之htmlviewhtml模块测试", TextViewHtmlDemoActivity.class);
		addButton("utils之IntentUtils工具类测试", IntentDemoActivity.class);
		addButton("utils之DialogUtils工具类测试", DialogUtilsDemoActivity.class);
		addButton("utils之ScreenshotUtils工具类测试", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String savePath = ContextUtils.getSdCardPath()
						+ "/bigapple/screenshot.png";
				ScreenshotUtils.shotView(content, savePath);
			}
		});
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

	private void addButton(String text,
			final Button.OnClickListener onClickListener) {
		Button button = new Button(BigappleMainActivity.this);
		button.setText(text);
		button.setOnClickListener(onClickListener);
		content.addView(button);
	}

}
