package com.xuan.bigapple.lib.ioc;

import android.app.Activity;
import android.view.View;

/**
 * 注入View的寻找器，只提供给BPViewUtils使用，所以是包内有效
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-10-28 下午5:37:24 $
 */
class ViewFinder {
	private View mView;
	private Activity mActivity;

	public ViewFinder(View view) {
		this.mView = view;
	}

	public ViewFinder(Activity activity) {
		this.mActivity = activity;
	}

	/**
	 * 从寻找器中找到指定的View
	 * 
	 * @param id
	 * @return
	 */
	public View findViewById(int id) {
		if (null != mView) {
			return mView.findViewById(id);
		}

		if (null != mActivity) {
			return mActivity.findViewById(id);
		}

		// 一般不会执行到这里
		return null;
	}

}
