package com.example.bigapple_demo.utils.clipboard;

import android.app.Activity;
import android.os.Bundle;

import com.example.bigapple_demo.R;
import com.winupon.andframe.bigapple.utils.ToastUtils;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_layout_test);

        ClipboardUtils.copyText(this, "222");
        ClipboardUtils.copyText(this, "666");

        ToastUtils.displayTextShort(this, ClipboardUtils.pasteText(this));

    }

}
