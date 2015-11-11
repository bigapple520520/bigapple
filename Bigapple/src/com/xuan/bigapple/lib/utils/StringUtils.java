package com.xuan.bigapple.lib.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;

/**
 * 字符串工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-13 下午2:18:14 $
 */
public abstract class StringUtils {
	public static final String UTF8 = "utf-8";
	public static final String ISO88591 = "iso8859-1";
	public static final String EMPTY = "";

	/**
	 * 比较两个字符串
	 * 
	 * @param cs1
	 * @param cs2
	 * @return
	 */
	public static boolean equals(CharSequence cs1, CharSequence cs2) {
		return cs1 == null ? cs2 == null : cs1.equals(cs2);
	}

	/**
	 * trim并过滤null到空串
	 * 
	 * @param str
	 * @return
	 */
	public static String trimToEmpty(String str) {
		return str == null ? EMPTY : str.trim();
	}

	// ///////////////////////////////////////////////////join和split方法///////////////////////////////////////////////
	/**
	 * join数组
	 * 
	 * @param array
	 * @param separator
	 * @return
	 */
	public static String join(Object[] array, String separator) {
		if (null == array) {
			return null;
		}

		return join(array, separator, 0, array.length - 1);
	}

	/**
	 * join数组
	 * 
	 * @param array
	 * @param separator
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public static String join(Object[] array, String separator, int startIndex,
			int endIndex) {
		if (array == null) {
			return null;
		}

		int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return "";
		}

		StringBuilder buf = new StringBuilder(noOfItems * 16);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * join集合里的所有
	 * 
	 * @param iterator
	 * @param separator
	 * @return
	 */
	public static String join(Iterator<?> iterator, String separator) {
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return "";
		}
		Object first = iterator.next();
		if (!iterator.hasNext()) {
			return first == null ? "" : first.toString();
		}

		// 含有两个以上的元素
		StringBuilder buf = new StringBuilder(256);// java默认是16个，可能太小了
		if (first != null) {
			buf.append(first);
		}

		while (iterator.hasNext()) {
			if (separator != null) {
				buf.append(separator);
			}
			Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}

	/**
	 * 分隔字符串，max默认全部-1，首尾有分隔符不要空串
	 * 
	 * @param str
	 * @param separatorChars
	 * @return
	 */
	public static String[] split(String str, String separatorChars) {
		return splitWorker(str, separatorChars, -1, false);
	}

	/**
	 * 分隔字符串
	 * 
	 * @param str
	 *            要分隔的字符串
	 * @param separatorChars
	 *            分隔符
	 * @param max
	 *            分隔出最多个数
	 * @param preserveAllTokens
	 *            当分隔符在首尾时，是否需要在首尾加空串
	 * @return
	 */
	public static String[] splitWorker(String str, String separatorChars,
			int max, boolean preserveAllTokens) {
		if (str == null) {
			return null;
		}

		int len = str.length();
		if (len == 0) {
			return new String[0];
		}

		List<String> list = new ArrayList<String>();
		int sizePlus1 = 1;
		int i = 0, start = 0;
		boolean match = false;// true表示有要分隔的内容
		boolean lastMatch = false;// true表示匹配到分隔符
		if (separatorChars == null) {
			// null作为分隔符就表示用空格作为分隔
			while (i < len) {
				if (Character.isWhitespace(str.charAt(i))) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else if (separatorChars.length() == 1) {
			// 分隔符为一个字符的情况下
			char sep = separatorChars.charAt(0);
			while (i < len) {
				if (str.charAt(i) == sep) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else {
			// 标准情况
			while (i < len) {
				if (separatorChars.indexOf(str.charAt(i)) >= 0) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		}

		// 如果有匹配内容 || 匹配到分隔符且首尾分隔符要空格
		if (match || (preserveAllTokens && lastMatch)) {
			list.add(str.substring(start, i));
		}

		return list.toArray(new String[list.size()]);
	}

	// ///////////////////////////////////////////////////UTF-8编码解码方法///////////////////////////////////////////////
	/**
	 * 编码UTF-8
	 * 
	 * @param str
	 * @return
	 */
	public static String encodeToUTF8(String str) {
		if (TextUtils.isEmpty(str)) {
			return str;
		}

		return newString(StringUtils.getBytes(str, ISO88591), UTF8);
	}

	/**
	 * 字节数组转String
	 * 
	 * @param bs
	 * @param charset
	 * @return
	 */
	public static String newString(byte[] bs, String charset) {
		try {
			String str = new String(bs, charset);
			return str;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * String转字节数组，图片要用：ISO8859-1编码
	 * 
	 * @param str
	 * @param charsetName
	 * @return
	 */
	public static byte[] getBytes(String str, String charsetName) {
		if (TextUtils.isEmpty(str) || TextUtils.isEmpty(charsetName)) {
			return null;
		}

		try {
			return str.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 截取固定长度的字符串，超长部分用suffix代替，最终字符串真实长度不会超过maxLength.
	 * 
	 * @param str
	 * @param maxLength
	 * @param suffix
	 * @return
	 */
	public static String cutOut(String str, int maxLength, String suffix) {
		if (Validators.isEmpty(str)) {
			return str;
		}

		int byteIndex = 0;
		int charIndex = 0;

		while (charIndex < str.length() && byteIndex <= maxLength) {
			char c = str.charAt(charIndex);
			if (c >= 256) {
				byteIndex += 2;
			} else {
				byteIndex++;
			}
			charIndex++;
		}

		if (byteIndex <= maxLength) {
			return str;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(str.substring(0, charIndex));
		sb.append(suffix);

		while (getRealLength(sb.toString()) > maxLength) {
			sb.deleteCharAt(--charIndex);
		}

		return sb.toString();
	}

	/**
	 * 取得字符串的真实长度，一个汉字长度算两个字节
	 * 
	 * @param str
	 *            字符串
	 * @return 字符串的字节数
	 */
	public static int getRealLength(String str) {
		if (str == null) {
			return 0;
		}

		char separator = 256;
		int realLength = 0;

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) >= separator) {
				realLength += 2;
			} else {
				realLength++;
			}
		}
		return realLength;
	}

	/**
	 * 如果str空就返回default值
	 * 
	 * @param str
	 * @param defaultStr
	 * @return
	 */
	public static String defaultIfEmpty(String str, String defaultStr) {
		return TextUtils.isEmpty(str) ? defaultStr : str;
	}

}
