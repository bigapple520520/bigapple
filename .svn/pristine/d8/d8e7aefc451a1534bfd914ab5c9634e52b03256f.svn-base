/* 
 * @(#)PinyinDemoActivity.java    Created on 2014-5-6
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.zzdemo.utils;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.winupon.andframe.bigapple.R;
import com.winupon.andframe.bigapple.ioc.AnActivity;
import com.winupon.andframe.bigapple.ioc.InjectView;
import com.winupon.andframe.bigapple.utils.ToastUtils;
import com.winupon.andframe.bigapple.utils.Validators;
import com.winupon.andframe.bigapple.utils.pinyin.PinyinUtil;

/**
 * 拼音测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-5-6 下午1:05:46 $
 */
public class PinyinDemoActivity extends AnActivity {

    @InjectView(R.id.editText)
    private EditText editText;

    @InjectView(R.id.button)
    private Button button;

    @InjectView(R.id.textView)
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_utils_pinyin_main);

        StringBuilder sb = new StringBuilder();
        char ni = '你';
        sb.append("测试‘你’\n");
        sb.append("全拼原串：" + PinyinUtil.toPinyin(PinyinDemoActivity.this, ni) + "\n");
        sb.append("全拼小写：" + PinyinUtil.toPinyinLower(PinyinDemoActivity.this, ni) + "\n");
        sb.append("全拼大写：" + PinyinUtil.toPinyinUpper(PinyinDemoActivity.this, ni) + "\n");
        sb.append("首字母原写：" + PinyinUtil.toPinyinF(PinyinDemoActivity.this, ni) + "\n");
        sb.append("首字母小写：" + PinyinUtil.toPinyinLowerF(PinyinDemoActivity.this, ni) + "\n");
        sb.append("首字母大写：" + PinyinUtil.toPinyinUpperF(PinyinDemoActivity.this, ni) + "\n");
        textView.setText(sb.toString());

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hanzi = editText.getText().toString();
                if (Validators.isEmpty(hanzi)) {
                    ToastUtils.displayTextShort(PinyinDemoActivity.this, "请输入汉字");
                    return;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("全拼原串：" + PinyinUtil.toPinyin(PinyinDemoActivity.this, hanzi) + "\n");
                sb.append("全拼小写：" + PinyinUtil.toPinyinLower(PinyinDemoActivity.this, hanzi) + "\n");
                sb.append("全拼大写：" + PinyinUtil.toPinyinUpper(PinyinDemoActivity.this, hanzi) + "\n");
                sb.append("首字母原写：" + PinyinUtil.toPinyinF(PinyinDemoActivity.this, hanzi) + "\n");
                sb.append("首字母小写：" + PinyinUtil.toPinyinLowerF(PinyinDemoActivity.this, hanzi) + "\n");
                sb.append("首字母大写：" + PinyinUtil.toPinyinUpperF(PinyinDemoActivity.this, hanzi) + "\n");
                textView.setText(sb.toString());
            }
        });
    }

}
