package com.example.bigapple_demo.ioc;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.bigapple_demo.R;
import com.winupon.andframe.bigapple.ioc.InjectView;
import com.winupon.andframe.bigapple.ioc.ViewUtils;

/**
 * 使用ViewUtils.inject(this);方式注解注入
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-18 下午3:37:24 $
 */
public class Main2 extends Activity {
    @InjectView(R.id.textView)
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ViewUtils.inject(this);

        textView.setText("你看我又被注入了吧");
    }

}
