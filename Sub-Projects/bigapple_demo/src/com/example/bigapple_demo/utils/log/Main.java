package com.example.bigapple_demo.utils.log;

import android.app.Activity;
import android.os.Bundle;

import com.example.bigapple_demo.R;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 简单的日志工具测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-18 下午3:37:24 $
 */
public class Main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LogUtils.d("debug test");
        LogUtils.allowD = false;// 设置后，左右debug日志不会输出，外网发布时可使用
        LogUtils.d("not print");
    }
}
