/* 
 * @(#)BitmapDemoActivity.java    Created on 2014-3-31
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.zzdemo.bitmap;

import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.winupon.andframe.R;
import com.winupon.andframe.bigapple.bitmap.AnBitmapUtilsFace;
import com.winupon.andframe.bigapple.bitmap.BitmapDisplayConfig;
import com.winupon.andframe.bigapple.bitmap.callback.ImageLoadCallBack;
import com.winupon.andframe.bigapple.ioc.InjectView;
import com.winupon.andframe.bigapple.ioc.app.AnActivity;
import com.winupon.andframe.bigapple.utils.ContextUtils;

/**
 * 网络图片加载测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-3-31 下午12:05:35 $
 */
public class BitmapDemoActivity extends AnActivity {
    private String[] pics = new String[20];

    // 2M
    private String url1 = "http://img.wallba.com/data/Image/2013pq/3yue/26hao/6/8/2013326105911734.jpg";
    // 100K
    private String url2 = "http://s1.cubexp.com/image/ec8/617a397032c8dfc5ec8054e638ed15cc.jpg";
    // 27K
    private String url3 = "http://a.hiphotos.bdimg.com/album/w%3D2048/sign=95e68b0b3801213fcf3349dc60df34d1/48540923dd54564e8aae5bebb2de9c82d0584f1c.jpg";
    private String url4 = ContextUtils.getSdCardPath() + "/xuan/222.jpg";

    @InjectView(R.id.button)
    private Button button;

    @InjectView(R.id.imageView)
    private ImageView imageView;

    @InjectView(R.id.textView)
    private TextView textView;

    @InjectView(R.id.textView2)
    private TextView textView2;

    @InjectView(R.id.textView3)
    private TextView textView3;

    @InjectView(R.id.gridView)
    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_bitmap_main);
        pics[0] = "http://d.hiphotos.baidu.com/baike/pic/item/37d12f2eb9389b50dcbf3a028735e5dde7116e1a.jpg";
        pics[1] = "http://img5.duitang.com/uploads/blog/201406/04/20140604212214_WJiUe.thumb.600_0.jpeg";
        pics[2] = "http://image.xinmin.cn/2014/01/28/20140128112210073789.jpg";
        pics[3] = "http://www.milaile.com/upload_omai/2014/01/omai1401180006390508.jpg";
        pics[4] = "http://image.xinmin.cn/2014/02/05/20140205115147725684.jpg";
        pics[5] = "http://www.big5.huanbohainews.com.cn/pic/0/11/37/13/11371306_931362.jpg";
        pics[6] = "http://www.ppys.net/uploads/allimg/140207/144Q2EH-0.jpg";
        pics[7] = "http://m1.biz.itc.cn/pic/new/n/24/51/Img6115124_n.jpg";
        pics[8] = "http://img5.duitang.com/uploads/blog/201406/04/20140604212352_EXCmN.thumb.700_0.jpeg";
        pics[9] = "http://img4.duitang.com/uploads/item/201401/24/20140124211608_cQVQ3.thumb.600_0.jpeg";
        pics[10] = "http://upload.hbtv.com.cn/2014/0123/1390442839230.jpg";
        pics[11] = "http://img1.imgtn.bdimg.com/it/u=4052436681,686876588&fm=11&gp=0.jpg";
        pics[12] = "http://img5.duitang.com/uploads/blog/201406/04/20140604212228_KSjUJ.thumb.600_0.jpeg";
        pics[13] = "http://www.cmen.cc/uploadfile/2014/0205/20140205111043961.jpg";
        pics[14] = "http://image.s1979.com/allimg/140122/29-140122120358.jpg";
        pics[15] = "http://img4.duitang.com/uploads/blog/201403/06/20140306183931_z3uXU.thumb.600_0.jpeg";
        pics[16] = "http://japan.people.com.cn/NMediaFile/2014/0128/MAIN201401280912000235805370582.jpeg";
        pics[17] = "http://pic.faxingfeng.com/2014/0207/qRHj1391770831.jpg";
        pics[18] = "http://clubfiles.liba.com/2014/03/13/00/39120151.jpg";
        pics[19] = "http://upload.hbtv.com.cn/2014/0123/1390442839268.jpg";

        AnBitmapUtilsFace.init(this);// 只需要初始化一次即可

        // ///////////////////////////////////////////清理缓存///////////////////////////////////////////////////
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AnBitmapUtilsFace.getInstance().clearCacheAll();
            }
        });

        // //////////////////////////////////////////加载单个图片/////////////////////////////////////////////////////////////
        AnBitmapUtilsFace.getInstance().display(imageView, url3);

        // ////////////////////////////////////多图显示/////////////////////////////////////////////////////
        final long start = System.currentTimeMillis();

        final BitmapDisplayConfig multiConfig = new BitmapDisplayConfig();
        multiConfig.setShowOriginal(false);
        multiConfig.setBitmapMaxHeight(100);
        multiConfig.setBitmapMaxWidth(100);
        multiConfig.setRoundPx(100);

        final String[] urls = new String[20];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = pics[i];
        }

        final AtomicInteger count = new AtomicInteger(urls.length);
        multiConfig.setImageLoadCallBack(new ImageLoadCallBack() {
            @Override
            public void loadFailed(ImageView imageView, Bitmap bitmap) {
            }

            @Override
            public void loadCompleted(ImageView imageView, final Bitmap bitmap, BitmapDisplayConfig config) {
                imageView.setImageBitmap(bitmap);
                count.decrementAndGet();
                if (count.get() == 0) {
                    textView3.setText("多图显示一共耗时：" + ((System.currentTimeMillis() - start) / 1000) + "秒");
                }
            }
        });

        gridView.setAdapter(new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView = new ImageView(BitmapDemoActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
                imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_launcher));
                AnBitmapUtilsFace.getInstance().display(imageView, urls[position], multiConfig);
                return imageView;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public int getCount() {
                return urls.length;
            }
        });
    }
}
