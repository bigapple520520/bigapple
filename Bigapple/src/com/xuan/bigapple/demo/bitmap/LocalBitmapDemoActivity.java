package com.xuan.bigapple.demo.bitmap;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.xuan.bigapple.R;
import com.xuan.bigapple.lib.bitmap.BitmapDisplayConfig;
import com.xuan.bigapple.lib.bitmap.core.impl.local.LocalBitmapLoader;
import com.xuan.bigapple.lib.ioc.InjectView;
import com.xuan.bigapple.lib.ioc.app.BPActivity;
import com.xuan.bigapple.lib.utils.ContextUtils;

/**
 * 本地图片加载测试
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-3-31 下午12:05:35 $
 */
public class LocalBitmapDemoActivity extends BPActivity {
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
		LocalBitmapLoader.init(this);

		initPicUrl();

		// 清理缓存
		clearCacheBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LocalBitmapLoader.getInstance().clearCacheAll(null);
			}
		});

		// 刷新设配器
		refreshListBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				gridViewAdapter.notifyDataSetChanged();
			}
		});

		gridViewAdapter = new GridViewAdapter();
		gridView.setAdapter(gridViewAdapter);
	}

	// 初始化图片
	private void initPicUrl() {
		picUrlList = new ArrayList<String>();
		String sdPath = ContextUtils.getSdCardPath();
		for (int i = 1; i <= 6; i++) {
			picUrlList.add(sdPath + "/xuan/" + i + ".png");
		}
	}

	class GridViewAdapter extends BaseAdapter {
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
			ImageView image = new ImageView(LocalBitmapDemoActivity.this);
			image.setLayoutParams(new GridView.LayoutParams(100, 100));

			BitmapDisplayConfig c = new BitmapDisplayConfig();
			c.setBitmapMaxHeight(100);
			c.setBitmapMaxWidth(100);

			LocalBitmapLoader.getInstance().display(image,
					picUrlList.get(position), c);
			return image;
		}
	}

}
