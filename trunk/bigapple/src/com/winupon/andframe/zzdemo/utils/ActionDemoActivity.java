/* 
 * @(#)ActionDemoActivity.java    Created on 2014-12-8
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.zzdemo.utils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.winupon.andframe.bigapple.ioc.app.AnActivity;
import com.winupon.andframe.bigapple.utils.ActionUtils;

/**
 * 一般调用系统自带功能调用
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-12-8 下午2:04:23 $
 */
public class ActionDemoActivity extends AnActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        setContentView(root);

        // 选择文件器
        Button button1 = new Button(this);
        button1.setText("选择文件器");
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ActionUtils.showFileChooser(ActionDemoActivity.this, 1);
            }
        });
        root.addView(button1);
    }

}
