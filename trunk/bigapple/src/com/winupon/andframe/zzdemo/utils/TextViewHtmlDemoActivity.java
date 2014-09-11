/* 
 * @(#)TextViewHtmlDemoActivity.java    Created on 2014-8-15
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.zzdemo.utils;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.winupon.andframe.R;
import com.winupon.andframe.bigapple.ioc.InjectView;
import com.winupon.andframe.bigapple.ioc.app.AnActivity;
import com.winupon.andframe.bigapple.utils.ToastUtils;
import com.winupon.andframe.bigapple.utils.textviewhtml.span.SpannableStringUtils;

/**
 * TextView文本处理
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-8-15 上午11:45:16 $
 */
public class TextViewHtmlDemoActivity extends AnActivity {

    @InjectView(R.id.text1)
    private TextView text1;

    @InjectView(R.id.text2)
    private TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_utils_textviewhtml_main);

        text1.setMovementMethod(LinkMovementMethod.getInstance());
        text1.setText("好多哈偶的好多哈偶的好多哈偶的好多哈偶的好多哈偶的好多哈偶的好多哈偶的");
        text1.append(SpannableStringUtils.getOnClickSpan("点我啊", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.displayTextShort(TextViewHtmlDemoActivity.this, "111111");
            }
        }));
    }

}
