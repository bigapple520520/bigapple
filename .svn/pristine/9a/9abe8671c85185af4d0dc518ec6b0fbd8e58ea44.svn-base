package com.example.bigapple_demo.db;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.bigapple_demo.R;
import com.winupon.andframe.bigapple.db.DBHelper;

public class Main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_layout_test);

        DBHelper.init(1, "bigapple_test_database");// 只需要初始化一次，可放到Application中去

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                test1();
            }
        });
    }

    private void test1() {
        long start1 = System.currentTimeMillis();
        TestDao testDao = new TestDao(Main.this);

        for (int i = 0; i < 10; i++) {
            testDao.insertTest("222");
        }

        Log.d("", "--------------------------------start1:" + (System.currentTimeMillis() - start1));

        TestDaoAdapter testDaoAdapter = new TestDaoAdapter(Main.this);
        long start2 = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            testDaoAdapter.insertTest("444");
        }

        Log.d("", "--------------------------------start2:" + (System.currentTimeMillis() - start2));
        testDaoAdapter.close();
    }

    private void test2() {
        long start1 = System.currentTimeMillis();
        TestDao testDao = new TestDao(Main.this);

        testDao.insertBatchTest();

        Log.d("", "--------------------------------start1:" + (System.currentTimeMillis() - start1));

        TestDaoAdapter testDaoAdapter = new TestDaoAdapter(Main.this);
        long start2 = System.currentTimeMillis();

        testDaoAdapter.insertBatchTest();

        Log.d("", "--------------------------------start2:" + (System.currentTimeMillis() - start2));
        testDaoAdapter.close();
    }

    private void test3() {
        final long start1 = System.currentTimeMillis();
        final TestDao testDao1 = new TestDao(Main.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    testDao1.insertTest("444");
                }
                Log.d("", "--------------------------------start1:" + (System.currentTimeMillis() - start1));
            }
        }).start();

        final long start2 = System.currentTimeMillis();
        final TestDao testDao2 = new TestDao(Main.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    testDao2.insertTest("222");
                }
                Log.d("", "--------------------------------start2:" + (System.currentTimeMillis() - start2));
            }
        }).start();
    }

    private void test4() {
        final long start1 = System.currentTimeMillis();
        final TestDaoAdapter testDaoAdapter1 = new TestDaoAdapter(Main.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    testDaoAdapter1.insertTest("444");
                }
                testDaoAdapter1.close();
                Log.d("", "--------------------------------start1:" + (System.currentTimeMillis() - start1));
            }
        }).start();

        final TestDaoAdapter testDaoAdapter2 = new TestDaoAdapter(Main.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    // testDaoAdapter1.insertTest("222");
                }
                // testDaoAdapter1.close();
            }
        }).start();
    }

    private void test5() {
        final TestDaoAdapter testDaoAdapter1 = new TestDaoAdapter(Main.this);
        final TestDaoAdapter testDaoAdapter2 = new TestDaoAdapter(Main.this);

    }

}
