package com.winupon.andframe.bigapple.zzdemo.db;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.winupon.andframe.bigapple.R;
import com.winupon.andframe.bigapple.ioc.AnActivity;
import com.winupon.andframe.bigapple.ioc.InjectView;
import com.winupon.andframe.bigapple.utils.DateUtils;

/**
 * 本地数据库操作，记得去MyApplication初始化数据库
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-3-21 下午4:23:16 $
 */
public class DbDemoActivity extends AnActivity {
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

    @InjectView(R.id.tips)
    private TextView tips;

    private final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_db_main);

        // 批量插入
        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBatchTest();
            }
        });

        // 删除所有
        button2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTest();
            }
        });

        // 查找所有
        button3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAllTest();
            }
        });

        // 多线程插入
        button4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBatchTest2();
            }
        });

        // Adapter多线程插入
        button5.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 得出结论：<br>
                 * 1、同一个实例关闭close会受到影响，即close了再去调用操作方法会报错<br>
                 * 2、不同实例关闭close不会受到影响，但是多个线程访问的时候会有问题，为底层使用的是对象锁<br>
                 * 
                 * 总结：<br>
                 * 如果使用单例操作，那么要注意close之后就不要再使用不然报错，如果使用多例操作，那么要注意多线程的安全
                 */
                testDaoAdapter();
            }
        });

        // dao2多线程插入
        button6.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDao2Muti();
            }
        });
    }

    private void testDao2Muti() {
        final long start1 = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    DaoFactory.getTestDao2().insertTest("我是线程1插入的");
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tips.setText(tips.getText() + "\n线程2完成插入，时间：" + (System.currentTimeMillis() - start1) + "ms");
                    }
                });
            }
        }).start();

        final long start2 = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    DaoFactory.getTestDao2().insertTest("我是线程2插入的");
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tips.setText(tips.getText() + "\n线程2完成插入，时间：" + (System.currentTimeMillis() - start2) + "ms");
                    }
                });
            }
        }).start();
    }

    private void testDaoAdapter() {
        final long start1 = System.currentTimeMillis();
        final TestDaoAdapter testDao1 = new TestDaoAdapter(DbDemoActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    testDao1.insertTest("我是线程1插入的");
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tips.setText(tips.getText() + "\n线程2完成插入，时间：" + (System.currentTimeMillis() - start1) + "ms");
                    }
                });
            }
        }).start();

        final long start2 = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    testDao1.insertTest("我是线程2插入的");
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tips.setText(tips.getText() + "\n线程2完成插入，时间：" + (System.currentTimeMillis() - start2) + "ms");
                    }
                });
            }
        }).start();
    }

    private void insertBatchTest() {
        long start1 = System.currentTimeMillis();
        TestDao testDao = new TestDao(DbDemoActivity.this);
        testDao.insertBatchTest();
        tips.setText("批量插入1000条数据花费的时间：" + (System.currentTimeMillis() - start1) + "ms");
    }

    private void deleteTest() {
        long start1 = System.currentTimeMillis();
        TestDao testDao = new TestDao(DbDemoActivity.this);
        testDao.deleteTest();
        tips.setText("删除所有数据花费的时间：" + (System.currentTimeMillis() - start1) + "ms");
    }

    private void findAllTest() {
        final long start1 = System.currentTimeMillis();
        final TestDao testDao = new TestDao(DbDemoActivity.this);
        List<User> userList = testDao.findAllUser();

        StringBuilder sb = new StringBuilder();
        for (User user : userList) {
            sb.append("id=" + user.getId() + "\n");
            sb.append("name=" + user.getName() + "\n");
            sb.append("creationTime=" + DateUtils.date2StringBySecond(user.getCretaionTime()) + "\n");
            sb.append("\n");
        }

        tips.setText("[最后花掉的时间是：" + (System.currentTimeMillis() - start1) + "ms]\n" + sb.toString());
    }

    private void insertBatchTest2() {
        final long start1 = System.currentTimeMillis();
        final TestDao testDao1 = new TestDao(DbDemoActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    testDao1.insertTest("我是线程1插入的");
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tips.setText(tips.getText() + "\n线程1完成插入，时间：" + (System.currentTimeMillis() - start1) + "ms");
                    }
                });
            }
        }).start();

        final long start2 = System.currentTimeMillis();
        final TestDao testDao2 = new TestDao(DbDemoActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    testDao2.insertTest("我是线程2插入的");
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tips.setText(tips.getText() + "\n线程2完成插入，时间：" + (System.currentTimeMillis() - start2) + "ms");
                    }
                });
            }
        }).start();
    }

}
