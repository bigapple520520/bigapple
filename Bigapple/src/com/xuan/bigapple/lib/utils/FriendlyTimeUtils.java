package com.xuan.bigapple.lib.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 友好时间显示工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-6-21 下午3:04:51 $
 */
public abstract class FriendlyTimeUtils {
	private static final int DAY_INTER = 24 * 60 * 60 * 1000;
	private static final int HOUR_INTER = 60 * 60 * 1000;
	private static final int MINUTE_INTER = 60 * 1000;
	private static final int SECOND_INTER = 1000;

	private final static ThreadLocal<SimpleDateFormat> dateFormater1 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yy-MM-dd");
		}
	};

	/**
	 * 规则如下，和当前时间之差：<br>
	 * 
	 * 0、如果小于1秒钟内，显示刚刚<br>
	 * 1、如果在1分钟内，显示XXX秒前<br>
	 * 2、如果在1小时内，显示XXX分钟前<br>
	 * 3、如果在1小时外的今天内，显示今天15:32<br>
	 * 4、如果是昨天的，显示昨天15:32<br>
	 * 5、其余显示，13-10-15<br>
	 * 6、时间不合法的情况下显示：年-月-日，如25-10-21
	 * 
	 * @param time
	 * @return
	 */
	public static String friendlyTime(Date time) {
		if (null == time) {
			return "unknown";
		}

		String ftime = "";
		Calendar cal = Calendar.getInstance();
		long intervals = cal.getTimeInMillis() - time.getTime();
		if (intervals < 0) {
			return dateFormater2.get().format(time);
		}

		long ltd = time.getTime() / DAY_INTER;
		long ctd = cal.getTimeInMillis() / DAY_INTER;
		int days = (int) (ctd - ltd);
		if (days == 0) { // 今天
			long lth = time.getTime() / HOUR_INTER;
			long cth = cal.getTimeInMillis() / HOUR_INTER;
			int hours = (int) (cth - lth);
			if (hours == 0) {// 1小时内
				long ltm = time.getTime() / MINUTE_INTER;
				long ctm = cal.getTimeInMillis() / MINUTE_INTER;
				int minutes = (int) (ctm - ltm);
				if (minutes == 0) {
					long lts = time.getTime() / SECOND_INTER;
					long cts = cal.getTimeInMillis() / SECOND_INTER;
					int seconds = (int) (cts - lts);
					if (seconds <= 0) {
						ftime = "刚刚";
					} else {
						ftime = seconds + "秒前";
					}
				} else {
					ftime = minutes + "分钟前";
				}
			} else {
				ftime = "今天  " + dateFormater1.get().format(time);
			}
		} else if (days == 1) {// 昨天
			ftime = "昨天  " + dateFormater1.get().format(time);
		} else {// 其余时间
			ftime = dateFormater2.get().format(time);
		}

		return ftime;
	}

}
