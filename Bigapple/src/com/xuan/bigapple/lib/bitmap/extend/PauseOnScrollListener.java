package com.xuan.bigapple.lib.bitmap.extend;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.xuan.bigapple.lib.bitmap.core.impl.IBitmapLoader;

/**
 * OnScrollListener针对AnBitmapUtils工具的实现。<br>
 * 作用：快速滑动时可设置不加载图片，防止卡顿现象
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午3:44:51 $
 */
public class PauseOnScrollListener implements OnScrollListener {
	/** 图片加载器 */
	private final IBitmapLoader bitmapLoader;
	/**
	 * 标识滚动时是否停止下载
	 */
	private final boolean pauseOnScroll;

	/**
	 * 标识手指做抛的动作时，是否停止下载
	 */
	private final boolean pauseOnFling;

	/**
	 * 外部自定义的OnScrollListener监听实现
	 */
	private final OnScrollListener externalListener;

	public PauseOnScrollListener(IBitmapLoader bitmapLoader,
			boolean pauseOnScroll, boolean pauseOnFling) {
		this(bitmapLoader, pauseOnScroll, pauseOnFling, null);
	}

	public PauseOnScrollListener(IBitmapLoader bitmapLoader,
			boolean pauseOnScroll, boolean pauseOnFling,
			OnScrollListener customListener) {
		this.bitmapLoader = bitmapLoader;
		this.pauseOnScroll = pauseOnScroll;
		this.pauseOnFling = pauseOnFling;
		externalListener = customListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			bitmapLoader.resumeTasks();
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			if (pauseOnScroll) {
				bitmapLoader.pauseTasks();
			}
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			if (pauseOnFling) {
				bitmapLoader.pauseTasks();
			}
			break;
		}

		if (externalListener != null) {
			externalListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (externalListener != null) {
			externalListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

}
