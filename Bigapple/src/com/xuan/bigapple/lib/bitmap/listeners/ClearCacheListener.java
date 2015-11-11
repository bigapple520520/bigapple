package com.xuan.bigapple.lib.bitmap.listeners;

/**
 * 清空缓存后会被回调的监听
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-13 下午5:19:29 $
 */
public interface ClearCacheListener {
	/**
	 * 清理缓存后调用，type参考BitmapCacheManagementTask
	 * 
	 * @param type
	 *            执行清理何种缓存的命令
	 * @param key
	 *            执行了哪个key的缓存
	 */
	public void afterClearCache(int type, String ley);

}
