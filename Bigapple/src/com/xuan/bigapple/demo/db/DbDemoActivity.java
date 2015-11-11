package com.xuan.bigapple.demo.db;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xuan.bigapple.R;
import com.xuan.bigapple.lib.ioc.InjectView;
import com.xuan.bigapple.lib.ioc.app.BPActivity;

/**
 * 本地数据库操作，记得去MyApplication初始化数据库
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-3-21 下午4:23:16 $
 */
public class DbDemoActivity extends BPActivity {
	@InjectView(R.id.button1)
	private Button button1;

	@InjectView(R.id.button2)
	private Button button2;

	@InjectView(R.id.button3)
	private Button button3;

	@InjectView(R.id.button4)
	private Button button4;

	@InjectView(R.id.button5)
	private Button button5;

	@InjectView(R.id.button6)
	private Button button6;

	@InjectView(R.id.button7)
	private Button button7;

	@InjectView(R.id.tips)
	private TextView tips;

	private final Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_db_main);

		// 批量插入
		button1.setText("插入测试");
		button1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				new UserDao().insertTest("aaaag");
			}
		});

		// 删除所有
		button2.setText("删除测试");
		button2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteTest();
			}
		});

		// 查找所有
		button3.setText("查找测试");
		button3.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				new UserDao().findTest();
			}
		});

		// 多线程插入
		button4.setText("多线程插入");
		button4.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				insertBatchTest2();
			}
		});
	}

	// 单线程批量插入
	private void insertBatchTest() {
		// long start1 = System.currentTimeMillis();
		// UserDao testDao = new UserDao();
		// testDao.insertBatchTest();
		// tips.setText("批量插入1条数据花费的时间：" + (System.currentTimeMillis() - start1)
		// + "ms");
	}

	// 删除所有
	private void deleteTest() {
		// long start1 = System.currentTimeMillis();
		// UserDao testDao = new UserDao();
		// testDao.deleteTest();
		// tips.setText("删除所有数据花费的时间：" + (System.currentTimeMillis() - start1)
		// + "ms");
	}

	// 查找所有
	private void findAllTest() {
		// final long start1 = System.currentTimeMillis();
		// final UserDao testDao = new UserDao();
		// List<User> userList = testDao.findAllUser();
		//
		// StringBuilder sb = new StringBuilder();
		// for (User user : userList) {
		// sb.append("id=" + user.getId() + "\n");
		// sb.append("name=" + user.getName() + "\n");
		// sb.append("creationTime="
		// + DateUtils.date2StringBySecond(user.getCretaionTime())
		// + "\n");
		// sb.append("\n");
		// }
		//
		// tips.setText("[最后花掉的时间是：" + (System.currentTimeMillis() - start1)
		// + "ms]\n" + sb.toString());
	}

	// 多线程插入
	private void insertBatchTest2() {
		final long start1 = System.currentTimeMillis();
		final UserDao userDao1 = new UserDao();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					userDao1.insertTest("我是线程1插入的");
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						tips.setText(tips.getText() + "\n线程1完成插入，时间："
								+ (System.currentTimeMillis() - start1) + "ms");
					}
				});
			}
		}).start();

		final long start2 = System.currentTimeMillis();
		final UserDao userDao2 = new UserDao();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					userDao2.insertTest("我是线程2插入的");
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						tips.setText(tips.getText() + "\n线程2完成插入，时间："
								+ (System.currentTimeMillis() - start2) + "ms");
					}
				});
			}
		}).start();
	}

}
