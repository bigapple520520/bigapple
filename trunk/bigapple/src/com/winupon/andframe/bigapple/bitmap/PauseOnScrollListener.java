package com.winupon.andframe.bigapple.bitmap;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * OnScrollListener针对AnBitmapUtils工具的实现。<br>
 * 作用：快速滑动时可设置不加载图片，防止卡顿现象
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午3:44:51 $
 */
public class PauseOnScrollListener implements OnScrollListener {
    private final AnBitmapUtils anBitmapUtils;
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

    public PauseOnScrollListener(AnBitmapUtils anBitmapUtils, boolean pauseOnScroll, boolean pauseOnFling) {
        this(anBitmapUtils, pauseOnScroll, pauseOnFling, null);
    }

    public PauseOnScrollListener(AnBitmapUtils anBitmapUtils, boolean pauseOnScroll, boolean pauseOnFling,
            OnScrollListener customListener) {
        this.anBitmapUtils = anBitmapUtils;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        externalListener = customListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
            anBitmapUtils.resumeTasks();
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            if (pauseOnScroll) {
                anBitmapUtils.pauseTasks();
            }
            break;
        case OnScrollListener.SCROLL_STATE_FLING:
            if (pauseOnFling) {
                anBitmapUtils.pauseTasks();
            }
            break;
        }

        if (externalListener != null) {
            externalListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (externalListener != null) {
            externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

}
