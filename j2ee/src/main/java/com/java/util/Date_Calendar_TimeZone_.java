package com.java.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

/**
 * java.util.Date	日期基本类，精确到毫秒。sql包扩展了3个子类对应3个SQL数据类型
 * 
 * 	java.sql.Date		年 月 日
 * 	java.sql.Time		时 分 秒（精确到毫秒）
 * 	java.sql.Timestamp	年 月 日 时 分 秒（精确到纳秒）
 * 
 * Calendar		日历，精确到毫秒
 * 
 * TimeZone 时区
 * 		GMT				格林威治标准时，根据地球的自转计算时间，太阳每天经过英国伦敦郊区的皇家格林威治天文台的时间就是中午12点
 * 			GMT+8		东8区
 * 		Etc/UTC			也作UTC，世界协调时，经过平均太阳时(以格林威治时间GMT为准)、地轴运动修正后的新时标以及以「秒」为单位的国际原子时所综合精算而成的时间。
 * 						其误差值必须保持在0.9秒以内，若大于0.9秒则由位于巴黎的国际地球自转事务中央局发布闰秒，使UTC与地球自转周期一致
 * 			UTC+8		东8区
 * 		Asia/Shanghai 	中国1986-1991年实行夏令时，夏天和冬天差1个小时，Asia/Shanghai会兼容这个时间段。夏季时使用东9区时间，夏季结束时再调回东8区时间
 * @author nerv
 *
 */
public class Date_Calendar_TimeZone_ {

	/**
	 * 将 java.sql.Timestamp 对象转换为精度达到毫秒量级的 java.util.Date对象：
	 */
	@Test
	public void timestampToDate() {
		// Date转Timestamp
	    Timestamp t = new Timestamp(new Date().getTime());
	    // Timestamp转Date，注意有纳秒的精度损失
	    Date d = new Date(t.getTime());
	}
	
	/**
	 * 日历
	 */
	@Test
	public void calendar() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;	// 从0开始
		int day = c.get(Calendar.DAY_OF_MONTH);
		System.out.println(year + "年" + month + "月" + day + "日");
	}
	
	/**
	 * 时区
	 */
	public void timeZone() {
		// 默认时区（操作系统时区）
        System.out.println(TimeZone.getDefault());
        // 设置JVM进程时区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
	}
}
