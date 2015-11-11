package com.xuan.bigapple.lib.utils.uuid;

import java.net.InetAddress;

/**
 * 随即串生成工具-供内部BPUUIDUtils使用，故设置成友好类
 * 
 * @author xuan
 * @version $Revision: 245 $, $Date: 2012-03-26 14:01:51 +0800 (星期一, 26 三月 2012)
 *          $
 */
class UUIDGenerator {
	private static final int IP;
	static {
		int ipadd;
		try {
			ipadd = toInt(InetAddress.getLocalHost().getAddress());
		} catch (Exception e) {
			ipadd = 0;
		}
		IP = ipadd;
	}
	private static short counter = (short) 0;
	private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

	public UUIDGenerator() {
	}

	public static String generateHex() {
		return new StringBuffer(36).append(format(getIP()))
				.append(format(getJVM())).append(format(getHighTime()))
				.append(format(getLowTime())).append(format(getCount()))
				.toString();
	}

	public static byte[] generateBytes() {
		byte[] bytes = new byte[16];
		System.arraycopy(getBytes(getIP()), 0, bytes, 0, 4);
		System.arraycopy(getBytes(getJVM()), 0, bytes, 4, 4);
		System.arraycopy(getBytes(getHighTime()), 0, bytes, 8, 2);
		System.arraycopy(getBytes(getLowTime()), 0, bytes, 10, 4);
		System.arraycopy(getBytes(getCount()), 0, bytes, 14, 2);
		return bytes;
	}

	private static String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	private static String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	/**
	 * Unique across JVMs on this machine (unless they load this class in the
	 * same quater second - very unlikely)
	 */
	private static int getJVM() {
		return JVM;
	}

	/**
	 * Unique in a millisecond for this JVM instance (unless there are >
	 * Short.MAX_VALUE instances created in a millisecond)
	 */
	private static short getCount() {
		synchronized (UUIDGenerator.class) {
			if (counter < 0) {
				counter = 0;
			}
			return counter++;
		}
	}

	/**
	 * Unique in a local network
	 */
	private static int getIP() {
		return IP;
	}

	/**
	 * Unique down to millisecond
	 */
	private static short getHighTime() {
		return (short) (System.currentTimeMillis() >>> 32);
	}

	private static int getLowTime() {
		return (int) System.currentTimeMillis();
	}

	private static int toInt(byte[] bytes) {
		int result = 0;
		for (int i = 0; i < 4; i++) {
			result = (result << 8) - Byte.MIN_VALUE + bytes[i];
		}
		return result;
	}

	private static byte[] getBytes(int intval) {
		return new byte[] { (byte) (intval >> 24), (byte) (intval >> 16),
				(byte) (intval >> 8), (byte) intval };
	}

	private static byte[] getBytes(short shortval) {
		return new byte[] { (byte) (shortval >> 8), (byte) shortval };
	}

	public static String toHexString(byte[] bytes) {
		StringBuffer hexString = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			hexString
					.append(enoughZero(Integer.toHexString(bytes[i] & 0xff), 2));
		}
		return hexString.toString();
	}

	public static String enoughZero(String str, int len) {
		while (str.length() < len) {
			str = "0" + str;
		}
		return str;
	}

	public static void main(String[] args) {
		// UUIDGenerator generator = new UUIDGenerator();
		System.out.println(UUIDGenerator.generateHex());
	}

}
