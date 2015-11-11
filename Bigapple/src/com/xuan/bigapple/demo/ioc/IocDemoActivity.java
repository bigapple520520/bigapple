package com.xuan.bigapple.demo.ioc;

import android.os.Bundle;
import android.widget.TextView;

import com.xuan.bigapple.R;
import com.xuan.bigapple.lib.ioc.InjectView;
import com.xuan.bigapple.lib.ioc.ViewUtils;
import com.xuan.bigapple.lib.ioc.app.BPActivity;

/**
 * 注解使用demo
 * 
 * @author xuan
 */
public class IocDemoActivity extends BPActivity {
	@InjectView(R.id.textView)
	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_ioc_main);
		ViewUtils.inject(this);

		textView.setText("我是用注解引用的1");
	}

}

/**
 * 用这种注解，可以不必强制的继承AnActivity
 * 
 * @author xuan
 */
// public class IocDemoActivity extends Activity {
// @InjectView(R.id.textView)
// private TextView textView;
//
// @Override
// public void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// setContentView(R.layout.demo_ioc_main);
// ViewUtils.inject(this);
//
// textView.setText("我是用注解引用的2");
// }
//
// }
