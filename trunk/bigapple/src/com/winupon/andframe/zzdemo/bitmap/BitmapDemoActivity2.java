/* 
 * @(#)BitmapDemoActivity.java    Created on 2014-3-31
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.zzdemo.bitmap;

import java.util.ArrayList;
import java.util.List;
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
 * 本地图片加载测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-3-31 下午12:05:35 $
 */
public class BitmapDemoActivity2 extends AnActivity {
    @InjectView(R.id.clearCacheBtn)
    private Button clearCacheBtn;
    
    @InjectView(R.id.refreshListBtn)
    private Button refreshListBtn;

    @InjectView(R.id.gridView)
    private GridView gridView;
    
    private GridViewAdapter gridViewAdapter;
    
    private List<String> picUrlList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_bitmap_main2);
        AnBitmapUtilsFace2.init(this);
        initPicUrl();

        //清理缓存
        clearCacheBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AnBitmapUtilsFace2.getInstance().clearCacheAll();
            }
        });
        
        //刷新缓存
        refreshListBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				gridViewAdapter.notifyDataSetChanged();
			}
		});

        gridViewAdapter = new GridViewAdapter(); 
        gridView.setAdapter(gridViewAdapter);
    }
    
    private void initPicUrl(){
    	picUrlList = new  ArrayList<String>();
    	String sdPath = ContextUtils.getSdCardPath();
    	for(int i=0;i<=10;i++){
    		picUrlList.add(sdPath+"/xuan/"+i+".jpg");
    	}
    }
    
    class GridViewAdapter extends BaseAdapter{
    	@Override
    	public int getCount() {
    		return picUrlList.size();
    	}
    	@Override
    	public Object getItem(int arg0) {
    		return null;
    	}
    	@Override
    	public long getItemId(int arg0) {
    		return 0;
    	}
    	@Override
    	public View getView(final int position, View arg1, ViewGroup arg2) {
    		ImageView image = new ImageView(BitmapDemoActivity2.this);
    		image.setLayoutParams(new GridView.LayoutParams(100, 100));
    		AnBitmapUtilsFace2.getInstance().display(image, picUrlList.get(position));
    		
    		image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					picUrlList.remove(position);
					gridViewAdapter.notifyDataSetChanged();
                }
			});
    		return image;
    	}
    }
    
}
