package com.example.bigapple_demo.asynctask;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bigapple_demo.R;
import com.winupon.andframe.bigapple.asynctask.callback.AsyncTaskFailCallback;
import com.winupon.andframe.bigapple.asynctask.callback.AsyncTaskSuccessCallback;
import com.winupon.andframe.bigapple.asynctask.helper.Result;

/**
 * 缓存模块测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-18 下午3:37:24 $
 */
public class Main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        DemoTask demoTask = new DemoTask(this);
        demoTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<String>() {
            @Override
            public void successCallback(Result<String> result) {
                // 如果返回Result中是true，那个这个方法就会被监听到
                Toast.makeText(Main.this, result.getValue(), Toast.LENGTH_SHORT).show();
            }
        });
        demoTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<String>() {
            @Override
            public void failCallback(Result<String> result) {
                // 如果返回Result中是false，那个这个方法就会被监听到
            }
        });
        demoTask.execute("hi xuan");
    }
}
