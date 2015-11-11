package com.xuan.bigapple.lib.utils;

import java.util.HashMap;

import android.os.Build.VERSION;

/**
 * SDK版本判断
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-12 下午7:28:17 $
 */
public abstract class SdkVersionUtils {
	public static final int SDK10_API1 = 1;// BASE
	public static final int SDK11_API2 = 2;// BASE_1_1
	public static final int SDK15_API3 = 3;// CUPCAKE
	public static final int SDK16_API4 = 4;// CUR_DEVELOPMENT
	public static final int SDK20_API5 = 5;// ECLAIR
	public static final int SDK201_API6 = 6;// ECLAIR_0_1
	public static final int SDK21_API7 = 7;// ECLAIR_MR1
	public static final int SDK122_API8 = 8;// FROYO

	public static final int SDK23_API9 = 9;// GINGERBREAD
	public static final int SDK233_API10 = 10;// GINGERBREAD_MR1

	public static final int SDK30_API11 = 11;// HONEYCOMB
	public static final int SDK31_API12 = 12;// HONEYCOMB_MR1
	public static final int SDK32_API13 = 13;// HONEYCOMB_MR2

	public static final int SDK40_API14 = 14;// ICE_CREAM_SANDWICH
	public static final int SDK403_API15 = 15;// ICE_CREAM_SANDWICH_MR1
	public static final int SDK41_API16 = 16;// JELLY_BEAN
	public static final int SDK42_API17 = 17;// JELLY_BEAN_MR1
	public static final int SDK43_API18 = 18;// JELLY_BEAN_MR2

	public static final int SDK44_API19 = 19;// KitKat

	private static HashMap<Integer, String> sdkInt2DescriptionMap = new HashMap<Integer, String>();
	static {
		sdkInt2DescriptionMap.put(SDK10_API1, "BASE");
		sdkInt2DescriptionMap.put(SDK11_API2, "BASE_1_1");
		sdkInt2DescriptionMap.put(SDK15_API3, "CUPCAKE");
		sdkInt2DescriptionMap.put(SDK16_API4, "CUR_DEVELOPMENT");
		sdkInt2DescriptionMap.put(SDK20_API5, "ECLAIR");
		sdkInt2DescriptionMap.put(SDK201_API6, "ECLAIR_0_1");
		sdkInt2DescriptionMap.put(SDK21_API7, "ECLAIR_MR1");
		sdkInt2DescriptionMap.put(SDK122_API8, "FROYO");

		sdkInt2DescriptionMap.put(SDK23_API9, "GINGERBREAD");
		sdkInt2DescriptionMap.put(SDK233_API10, "GINGERBREAD_MR1");

		sdkInt2DescriptionMap.put(SDK30_API11, "HONEYCOMB");
		sdkInt2DescriptionMap.put(SDK31_API12, "HONEYCOMB_MR1");
		sdkInt2DescriptionMap.put(SDK32_API13, "HONEYCOMB_MR2");

		sdkInt2DescriptionMap.put(SDK40_API14, "ICE_CREAM_SANDWICH");
		sdkInt2DescriptionMap.put(SDK403_API15, "ICE_CREAM_SANDWICH_MR1");
		sdkInt2DescriptionMap.put(SDK41_API16, "JELLY_BEAN");
		sdkInt2DescriptionMap.put(SDK42_API17, "JELLY_BEAN_MR1");
		sdkInt2DescriptionMap.put(SDK43_API18, "JELLY_BEAN_MR2");

		sdkInt2DescriptionMap.put(SDK44_API19, "KitKat");
	}

	/**
	 * 获取当前SDK的数字版本号
	 * 
	 * @return 当前SDK数字版本号
	 */
	public static int getCurrentSdkInt() {
		return VERSION.SDK_INT;
	}

	/**
	 * 获取当前SDK的版本描述
	 * 
	 * @return 返回当前SDK版本描述
	 */
	public static String getCurrentSdkDescription() {
		String description = sdkInt2DescriptionMap.get(VERSION.SDK_INT);
		if (null == description) {
			return "unknown";
		}
		return description;
	}

	/**
	 * 判断当前SDK数字版本号是否大于待比较版本号
	 * 
	 * @param sdkInt
	 *            待比较版本号
	 * @return 返回true表示是false表示不是
	 */
	public static boolean isUpper(int sdkInt) {
		return VERSION.SDK_INT > sdkInt;
	}

	/**
	 * 判断当前SDK数字版本号是否大于等于待比较版本号
	 * 
	 * @param sdkInt
	 *            待比较版本号
	 * @return 返回true表示是false表示不是
	 */
	public static boolean isUpperEquals(int sdkInt) {
		return VERSION.SDK_INT >= sdkInt;
	}

	/**
	 * 判断当前SDK数字版本号是否小于待比较版本号
	 * 
	 * @param sdkInt
	 *            待比较版本号
	 * @return 返回true表示是false表示不是
	 */
	public static boolean isLower(int sdkInt) {
		return VERSION.SDK_INT < sdkInt;
	}

	/**
	 * 判断当前SDK数字版本号是否小于等于待比较版本号
	 * 
	 * @param sdkInt
	 *            待比较版本号
	 * @return 返回true表示是false表示不是
	 */
	public static boolean isLowerEquals(int sdkInt) {
		return VERSION.SDK_INT <= sdkInt;
	}

	/**
	 * 判断当前SDK数字版本号是否等于待比较版本号
	 * 
	 * @param sdkInt
	 *            待比较版本号
	 * @return 返回true表示是false表示不是
	 */
	public static boolean isEquals(int sdkInt) {
		return sdkInt == VERSION.SDK_INT;
	}

}
