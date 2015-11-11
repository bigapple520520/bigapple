package com.xuan.bigapple.lib.utils.uuid;

/**
 * 随机串号生成
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2012-9-28 下午07:30:51 $
 */
public abstract class UUIDUtils {

	/**
	 * 生成32位的uuid字符串
	 * 
	 * @return 32位的uuid字符串
	 */
	public static String createId() {
		return UUIDGenerator.generateHex();
	}

}
