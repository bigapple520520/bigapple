package com.xuan.bigapple.demo.http;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xuan.bigapple.R;
import com.xuan.bigapple.lib.http.BPHttpUtils;
import com.xuan.bigapple.lib.http.BPResponse;
import com.xuan.bigapple.lib.ioc.InjectView;
import com.xuan.bigapple.lib.ioc.app.BPActivity;
import com.xuan.bigapple.lib.utils.ToastUtils;

/**
 * Http模块测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-1-2 下午6:52:41 $
 */
public class HttpDemoActivity extends BPActivity {
	@InjectView(R.id.getBtn)
	private Button getBtn;

	@InjectView(R.id.postBtn)
	private Button postBtn;

	@InjectView(R.id.textView)
	private TextView textView;

	private final Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_http_main);
		initGetBtn();
		initPostBtn();
	}

	private void initGetBtn() {
		getBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							final BPResponse bpResponse = BPHttpUtils.get(
									"http://www.baidu.com", null);
							handler.post(new Runnable() {
								@Override
								public void run() {
									textView.setText(bpResponse.toString());
								}
							});
						} catch (Exception e) {
							ToastUtils.displayTextShort(HttpDemoActivity.this,
									e.getMessage());
						}
					}
				}).start();
			}
		});
	}

	private void initPostBtn() {
		postBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							final BPResponse bpResponse = BPHttpUtils.post(
									"http://www.baidu.com", null);
							handler.post(new Runnable() {
								@Override
								public void run() {
									textView.setText(bpResponse.toString());
								}
							});
						} catch (Exception e) {
							ToastUtils.displayTextShort(HttpDemoActivity.this,
									e.getMessage());
						}
					}
				}).start();
			}
		});
	}

}
