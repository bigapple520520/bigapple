package com.winupon.andframe;

import java.io.File;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.winupon.andframe.bigapple.ioc.InjectView;
import com.winupon.andframe.bigapple.ioc.app.AnActivity;
import com.winupon.andframe.bigapple.utils.ContextUtils;
import com.winupon.andframe.bigapple.utils.ShareUtils;
import com.winupon.andframe.bigapple.utils.ToastUtils;
import com.winupon.andframe.bigapple.utils.updater.ApkUpdateConfig;
import com.winupon.andframe.bigapple.utils.updater.ApkUpdater;
import com.winupon.andframe.zzdemo.bitmap.BitmapDemoActivity;
import com.winupon.andframe.zzdemo.db.DbDemoActivity;
import com.winupon.andframe.zzdemo.http2.UrlHttpClientDemoActivity;
import com.winupon.andframe.zzdemo.ioc.IocDemoActivity;
import com.winupon.andframe.zzdemo.utils.ActionDemoActivity;
import com.winupon.andframe.zzdemo.utils.PinyinDemoActivity;
import com.winupon.andframe.zzdemo.utils.TextViewHtmlDemoActivity;

public class BigappleMainActivity extends AnActivity {

    @InjectView(R.id.content)
    private LinearLayout content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigapple_main);

        addButton("bitmap部分模块测试", BitmapDemoActivity.class);
        addButton("http2部分模块测试", UrlHttpClientDemoActivity.class);
        addButton("ioc部分模块测试", IocDemoActivity.class);
        addButton("db部分模块测试", DbDemoActivity.class);
        addButton("utils之pinyin模块测试", PinyinDemoActivity.class);
        addButton("utils之一键分享测试", new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.share(BigappleMainActivity.this, "不好意思我在测试android的一键分享功能",
                        Uri.fromFile(new File(ContextUtils.getSdCardPath() + "/xuan/1.jpg")));
            }
        });
        addButton("utils之update模块测试", new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UpdateManager updateManager = new UpdateManager(BigappleMainActivity.this);
                // updateManager.setDownloadFinishListener(new DownloadFinishListener() {
                // @Override
                // public void downloadFinish(String filePath) {
                // ToastUtils.displayTextShort(BigappleMainActivity.this, filePath);
                // }
                // });
                // updateManager.setUpdateCancelListener(new UpdateCancelListener() {
                // @Override
                // public void updateCancel(CancelEvent cancelEvent) {
                // ToastUtils.displayTextShort(BigappleMainActivity.this, "我被取消喽：" + cancelEvent.getEvent());
                // }
                // });
                // // updateManager.doDownload("http://res.wpstatic.cn/m/weike-test.apk", Environment
                // // .getExternalStorageDirectory().getPath() + "/xuan1/bigapple-default.apk");
                // updateManager.doDownload("http://gdown.baidu.com/data/wisegame/a279c52cd4acedb7/xiangji360_606.apk",
                // Environment.getExternalStorageDirectory().getPath() + "/xuan1/bigapple-default.apk");
                ApkUpdateConfig config = new ApkUpdateConfig();
                config.setProgressListener(new ApkUpdateConfig.ProgressListener() {
                    @Override
                    public boolean cancel(DialogInterface dialogInterface) {
                        ToastUtils.displayTextShort(BigappleMainActivity.this, "您已取消下载");
                        return false;
                    }
                });
                ApkUpdater.update(BigappleMainActivity.this,
                        "http://gdown.baidu.com/data/wisegame/a279c52cd4acedb7/xiangji360_606.apk", Environment
                                .getExternalStorageDirectory().getPath() + "/xuan1/bigapple-default.apk", null, config);
            }
        });
        addButton("utils之htmlviewhtml模块测试", TextViewHtmlDemoActivity.class);
        addButton("utils之ActionUtils工具类测试", ActionDemoActivity.class);
    }

    private void addButton(String text, final Class<?> clazz) {
        Button button = new Button(BigappleMainActivity.this);
        button.setText(text);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(BigappleMainActivity.this, clazz);
                startActivity(intent);
            }
        });
        content.addView(button);
    }

    private void addButton(String text, final Button.OnClickListener onClickListener) {
        Button button = new Button(BigappleMainActivity.this);
        button.setText(text);
        button.setOnClickListener(onClickListener);
        content.addView(button);
    }

}
