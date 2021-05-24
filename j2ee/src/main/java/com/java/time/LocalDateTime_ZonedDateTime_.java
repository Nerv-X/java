package com.java.time;

import java.time.*;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;
/**
 * LocalDateTime	对象只封装了时间，没有时区信息
 * 	静态
 * 		now()							当前时间，使用JVM进程的时区
 * 		now(zone)						当前时间，使用指定时区
 * 		atZone(zone)					根据指定时区返回ZonedDateTime
 * 		format(DateTimeFormat)			格式化为字符串
 * 		parse(String, DateTimeFormat)	将字符串转时间
 * 		get()							获取枚举类型对应时间项目值
 * LocalDate		只有年月日
 * LocalTime		只有时分秒
 * ZonedDateTime	包含时间和时区信息
 * @author nerv
 *
 */
public class LocalDateTime_ZonedDateTime_ {
	
	private final ZoneId D8 = ZoneId.of("+8");
	
	/**
	 * LocalDateTime查询东8区时间的方式（假设操作系统时区非东8区）
	 */
	@Test
	public void test1() {
		// 1.先设定JVM时区
		TimeZone.setDefault(TimeZone.getTimeZone("+8"));
		System.out.println(LocalDateTime.now());
		// 2.指定时区
		System.out.println(LocalDateTime.now(D8));
		// 3.指定时区转ZonedDateTime
		System.out.println(LocalDateTime.now().atZone(D8));
	}
	
	/**
	 * ZonedDateTime查询东8区时间的方式
	 */
	public void test2() {
		// 1.先设定JVM时区
		TimeZone.setDefault(TimeZone.getTimeZone("+8"));
		System.out.println(ZonedDateTime.now());
		// 2.指定时区
		System.out.println(ZonedDateTime.now(D8));
	}
	
	/**
	 * LocalDateTime转Date，需要先转为ZonedDateTime
	 * @param localDateTime
	 */
	public Date toDate(LocalDateTime localDateTime) {
		return toDate(localDateTime.atZone(ZoneId.of("UTC")));
	}
	
	/**
	 * ZonedDateTime转Date
	 * @param zonedDateTime
	 * @return
	 */
	public Date toDate(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
	}
	
	/**
	 * Date转ZonedDateTime，先转Instant
	 * @param date
	 * @return
	 */
	public ZonedDateTime toZonedDateTime(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		return instant.atZone(D8);
	}
	
	/**
	 * Date转LocalDateTime，先转ZonedDateTime
	 * @param date
	 * @return
	 */
	public LocalDateTime toLocalDate(Date date) {
		return toZonedDateTime(date).toLocalDateTime();
	}
}
