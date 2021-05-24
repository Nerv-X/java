package com.java.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

/**
 * 时间格式化
 * 
 * SimpleDateFormat		格式化Date，线程不安全
 * 		format()		日期转字符串，底层使用静态变量Calendar，线程不安全
 * 		parse()			字符串转日期，底层调用CalenderBuilder.establish()，其主要步骤非原子操作，线程不安全
 * 
 * 	安全的使用方式
 * 		1.用作局部变量
 * 		2.使用ThreadLocal
 * 		3.加锁synchronized(lock)
 * 		4.使用 DateTimeFormatter 代替SimpleDateFormat
 * 
 * DateTimeFormatter  	格式化ZonedDateTime和LocalDateTime，线程安全
 * @author nerv
 *
 */
public class SimpleDateFormat_DateTimeFormatter_ {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	
	/**
	 * 多线程转换时间，分别调用t1()、t2()测试
	 */
	@Test
	public void test() {
		ExecutorService poolExecutor = Executors.newFixedThreadPool(20);
		while (true) {
            poolExecutor.execute(() -> t2());
        }
	}
	/**
	 * SimpleDateFormat 线程不安全
	 */
	private void t1(){
		try {
			String s1 = sdf.format(new Date());
			Date parseDate = sdf.parse(s1);
			String s2 = sdf.format(parseDate);
			if (!s1.equals(s2))
	        	System.out.println(s1 + " " + "!= " + s2);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * DateTimeFormatter 线程安全
	 */
	private void t2() {
		LocalDateTime time1 = LocalDateTime.now();
		String strDate=time1.format(dtf);
		LocalDateTime time2 = LocalDateTime.parse(strDate, dtf);
		if (!time1.equals(time2))
			System.out.println(time1 + "不等于" + time2);
	}
}
