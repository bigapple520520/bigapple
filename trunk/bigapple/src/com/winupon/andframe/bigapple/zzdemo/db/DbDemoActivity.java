package com.winupon.andframe.bigapple.zzdemo.db;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.winupon.andframe.bigapple.R;
import com.winupon.andframe.bigapple.db.DBHelper;
import com.winupon.andframe.bigapple.ioc.AnActivity;
import com.winupon.andframe.bigapple.ioc.InjectView;

public class DbDemoActivity extends AnActivity {
	@InjectView(R.id.button1)
	private Button button1;

	@InjectView(R.id.button2)
	private Button button2;

	@InjectView(R.id.button3)
	private Button button3;

	@InjectView(R.id.button4)
	private Button button4;

	@InjectView(R.id.tips)
	private TextView tips;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_db_main);

		DBHelper.init(1, "bigapple_test_database");// 只需要初始化一次，一般正式的项目用发是放到自定义的Application中去

		button1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				insertBatchTest();
			}
		});

		button2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteTest();
			}
		});

		button3.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				findAllTest();
			}
		});

		button4.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				insertBatchTest2();
			}
		});
	}

	// 批量插入100条数据，记录花的时间
	private void insertBatchTest() {
		long start1 = System.currentTimeMillis();
		TestDao testDao = new TestDao(DbDemoActivity.this);
		testDao.insertBatchTest();

		tips.setText("插入1000条数据花费的时间：" + (System.currentTimeMillis() - start1)
				+ "ms");
	}

	// 删除测试，记录花的时间
	private void deleteTest() {
		long start1 = System.currentTimeMillis();
		TestDao testDao = new TestDao(DbDemoActivity.this);
		testDao.deleteTest();

		tips.setText("删除所有数据花费的时间：" + (System.currentTimeMillis() - start1)
				+ "ms");
	}

	// 测试查找
	private void findAllTest() {
		final long start1 = System.currentTimeMillis();
		final TestDao testDao = new TestDao(DbDemoActivity.this);
		List<User> userList = testDao.findAllUser();

		StringBuilder sb = new StringBuilder();
		for (User user : userList) {
			sb.append("id=" + user.getId() + "\n");
			sb.append("name=" + user.getName() + "\n");
			sb.append("\n");
		}

		tips.setText("[最后花掉的时间是：" + (System.currentTimeMillis() - start1)
				+ "ms]\n" + sb.toString());
	}

	// 多线程插入测试
	private void insertBatchTest2() {
		final long start1 = System.currentTimeMillis();
		final TestDao testDao1 = new TestDao(DbDemoActivity.this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 50; i++) {
					testDao1.insertTest("我是线程1插入的");
				}

				Log.d("",
						"--------------------------------start1:"
								+ (System.currentTimeMillis() - start1));
			}
		}).start();

		final long start2 = System.currentTimeMillis();
		final TestDao testDao2 = new TestDao(DbDemoActivity.this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 50; i++) {
					testDao2.insertTest("我是线程2插入的");
				}
				Log.d("",
						"--------------------------------start2:"
								+ (System.currentTimeMillis() - start2));
			}
		}).start();
	}

}
