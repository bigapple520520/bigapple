package com.example.bigapple_demo.utils.textviewhtml;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.example.bigapple_demo.R;
import com.example.bigapple_demo.utils.textviewhtml.SimpleURLSpan.UrlSpanOnClickListener;
import com.winupon.andframe.bigapple.utils.ToastUtils;

public class Main extends Activity {
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_layout_test);
        textView = (TextView) findViewById(R.id.textView);

        textView.setText(SpannableStringUtils.getBgColorSpan("背景", Color.BLACK));
        textView.append(SpannableStringUtils.getOnClickSpan("我是点击文本", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.displayTextShort(Main.this, "我被点击文本点击了");
            }
        }));

        textView.append(SpannableStringUtils.getUrlSpan("默认连接", "tel:15858178400", null));

        textView.append(SpannableStringUtils.getColorSpan("字体颜色", Color.BLACK));

        textView.append(SpannableStringUtils.getUrlSpan("自定义连接", "tel:15858178400", new UrlSpanOnClickListener() {
            @Override
            public void onClick(View widget, String url) {
                ToastUtils.displayTextShort(Main.this, url);
            }
        }));

        textView.append(SpannableStringUtils.getFontSpan("字体大小", 36));
        textView.append(SpannableStringUtils.getStyleSpan("字体样式", Typeface.BOLD_ITALIC));
        textView.append(SpannableStringUtils.getStrikeSpan("删除线"));
        textView.append(SpannableStringUtils.getUnderLineSpan("下滑线"));
        textView.append(SpannableStringUtils.getImageSpan(this, "图片", R.drawable.ic_launcher));
        textView.append(SpannableStringUtils.getSpan("自定义", new MyURLSpan("http://www.baidu.com")));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    class MyURLSpan extends URLSpan {
        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            ToastUtils.displayTextShort(Main.this, getURL());
        }
    }
}
