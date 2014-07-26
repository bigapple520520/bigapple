package com.example.bigapple_demo.ioc;

import android.os.Bundle;
import android.widget.TextView;

import com.example.bigapple_demo.R;
import com.winupon.andframe.bigapple.ioc.AnActivity;
import com.winupon.andframe.bigapple.ioc.InjectView;

/**
 * 使用继承的方式注入注解
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-18 下午3:37:24 $
 */
public class Main extends AnActivity {
    @InjectView(R.id.textView)
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_ioc);

        textView.setText("你看我被注入了吧");
    }
}
