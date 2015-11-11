package com.xuan.bigapple.lib.utils.pinyin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.text.TextUtils;
import android.util.Log;

/**
 * 中文转换拼音工具类，可以把中文转化成拼音，如果本身是字母或者数字就不变返回，不支持其他字符
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-5-6 下午12:56:34 $
 */
public abstract class PinyinUtil {
	private static final String TAG = PinyinUtil.class.getSimpleName();

	/**
	 * 把一个中文字节转成拼音，如果字节本身是英文，就原字母返回
	 * 
	 * @param context
	 * @param c
	 *            字节
	 * @return
	 */
	public static String toPinyin(char c) {
		if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
				|| (c >= '0' && c <= '9')) {
			// 字母或者数字原字返回
			return String.valueOf(c);
		}

		if (c == 0x3007) {
			return "ling";
		}

		if (c < 4E00 || c > 0x9FA5) {
			return null;
		}

		RandomAccessFile is = null;
		try {
			is = new RandomAccessFile(PinyinSource.getFile(), "r");
			long sp = (c - 0x4E00) * 6;
			is.seek(sp);
			byte[] buf = new byte[6];
			is.read(buf);
			return new String(buf).trim();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "文件未找到错误，原因：" + e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, "IO异常，原因：" + e.getMessage(), e);
		} finally {
			try {
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				// Ignore
			}
		}

		return null;
	}

	/**
	 * 把一个中文字节转成拼音的一个首字母，如果字节本身是英文，就原字母返回
	 * 
	 * @param c
	 *            字节
	 * @return
	 */
	public static String toPinyinF(char c) {
		String pinyin = toPinyin(c);
		if (null == pinyin) {
			return null;
		}

		return pinyin.substring(0, 1);
	}

	/**
	 * 把一个中文字节转成拼音，如果字节本身是英文，就统一返回小写
	 * 
	 * @param c
	 *            字节
	 * @return
	 */
	public static String toPinyinLower(char c) {
		String pinyin = toPinyin(c);
		if (null == pinyin) {
			return null;
		}

		return pinyin.toLowerCase();
	}

	/**
	 * 把一个中文字节转成拼音的一个首字母，如果字节本身是英文，就统一返回小写
	 * 
	 * @param c
	 *            字节
	 * @return
	 */
	public static String toPinyinLowerF(char c) {
		String pinyin = toPinyinLower(c);
		if (null == pinyin) {
			return null;
		}

		return pinyin.substring(0, 1);
	}

	/**
	 * 把一个中文字节转成拼音，如果字节本身是英文，就统一返回大写
	 * 
	 * @param c
	 *            字节
	 * @return
	 */
	public static String toPinyinUpper(char c) {
		String pinyin = toPinyin(c);
		if (null == pinyin) {
			return null;
		}

		return pinyin.toUpperCase();
	}

	/**
	 * 把一个中文字节转成拼音的一个首字母，如果字节本身是英文，就统一返回大写
	 * 
	 * @param c
	 *            字节
	 * @return
	 */
	public static String toPinyinUpperF(char c) {
		String pinyin = toPinyinUpper(c);
		if (null == pinyin) {
			return null;
		}

		return pinyin.substring(0, 1);
	}

	// /////////////////////////////////////////字符串////////////////////////////////////////////////////////////

	/**
	 * 把一个字符串转成拼音，如果字节本身是英文，就返回原大小写，多个中文字在转成拼音时会用空格间隔
	 * 
	 * @param hanzi
	 *            字符串
	 * @return
	 */
	public static String toPinyin(String hanzi) {
		if (TextUtils.isEmpty(hanzi)) {
			return null;
		}

		StringBuffer sb = new StringBuffer("");
		RandomAccessFile is = null;
		try {
			is = new RandomAccessFile(PinyinSource.getFile(), "r");
			for (int i = 0; i < hanzi.length(); i++) {
				char ch = hanzi.charAt(i);
				if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')
						|| (ch >= '0' && ch <= '9')) {
					sb.append(ch);
					continue;
				}

				if (ch == 0x3007) {
					sb.append("ling").append(' ');
				} else if (ch >= 0x4E00 || ch <= 0x9FA5) {
					long sp = (ch - 0x4E00) * 6;
					is.seek(sp);
					byte[] buf = new byte[6];
					is.read(buf);
					sb.append(new String(buf).trim()).append(' ');
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "IO异常，原因：" + e.getMessage(), e);
		} finally {
			try {
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				// Ignore
			}
		}
		return sb.toString().trim();
	}

	/**
	 * 把一个字符串转成拼音的首字母，如果字节本身是英文，就返回原大小写
	 * 
	 * @param hanzi
	 *            字符串
	 * @return
	 */
	public static String toPinyinF(String hanzi) {
		String pinyin = toPinyin(hanzi);
		if (null == pinyin) {
			return null;
		}

		return pinyin.substring(0, 1);
	}

	/**
	 * 把一个字符串转成拼音，如果字节本身是英文，就统一返回小写，多个中文字在转成拼音时会用空格间隔
	 * 
	 * @param hanzi
	 *            字符串
	 * @return
	 */
	public static String toPinyinLower(String hanzi) {
		String pinyin = toPinyin(hanzi);
		if (null == pinyin) {
			return null;
		}

		return pinyin.toLowerCase();
	}

	/**
	 * 把一个字符串转成拼音的首字母，如果字节本身是英文，就统一返回小写
	 * 
	 * @param hanzi
	 *            字符串
	 * @return
	 */
	public static String toPinyinLowerF(String hanzi) {
		String pinyin = toPinyinLower(hanzi);
		if (null == pinyin) {
			return null;
		}

		return pinyin.substring(0, 1);
	}

	/**
	 * 把一个字符串转成拼音，如果字节本身是英文，就统一返回大写，多个中文字在转成拼音时会用空格间隔
	 * 
	 * @param hanzi
	 *            字符串
	 * @return
	 */
	public static String toPinyinUpper(String hanzi) {
		String pinyin = toPinyin(hanzi);
		if (null == pinyin) {
			return null;
		}

		return pinyin.toUpperCase();
	}

	/**
	 * 把一个字符串转成拼音的首字母，如果字节本身是英文，就统一返回大写
	 * 
	 * @param hanzi
	 *            字符串
	 * @return
	 */
	public static String toPinyinUpperF(String hanzi) {
		String pinyin = toPinyinUpper(hanzi);
		if (null == pinyin) {
			return null;
		}

		return pinyin.substring(0, 1);
	}

}
