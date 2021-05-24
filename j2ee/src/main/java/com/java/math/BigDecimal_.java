package com.java.math;

import java.math.BigDecimal;
/**
 * 大数字浮点型，每次运算结果返回新对象
 * 	构造：支持double、long、int、String、char[]转为BigDecimal。其中double转后的值有精度误差，最好不用
 * 	静态
 * 		valueOf()				其它类型转BigDecimal，转double无精度问题
 * 		add()					加法
 * 		min()					减法
 * 		multiply()				乘法
 * 		divide(value, scale, roundingMode)	除法，指定结果小数位数，精度模式
 * 		abs()								绝对值
 * 
 * 精度模式
 * 		BigDecimal.ROUND_HALF_UP 	四舍五入
 * 		
 */
import org.junit.Test;
/**
 * BigDecimal
 * 	构造方法
 * 		BigDecimal(int)
 * 		BigDecimal(double)		※	勿用！有精度损失
 * 		BigDecimal(long)
 * 		BigDecimal(String)
 * 	实例方法
 * 		add()			加
 * 		subtract()		减
 * 		multiply()		乘
 * 		divide()		除
 * 		valueOf()		其它类型转BigDecimal
 * 	
 * 	静态常量
 * 		ROUND_HALF_UP		精度模式-四舍五入	1.5->2
 * 		ROUND_HALF_DOWN		精度模式-向下取整	1.5->1
 * @author nerv
 *
 */
public class BigDecimal_ {

	/**
	 * double转BigDecimal：valueOf(double)或new BigDecimal(string)
	 */
	@Test
	public void doubleToBigDecimal() {
		double d = 57.3;
		System.out.println("BigDecimal(double)有精度损失：" + new BigDecimal(d));
		System.out.println("valueOf(double)准确：" + BigDecimal.valueOf(d));
		System.out.println("BigDecimal(string)准确：" + new BigDecimal(String.valueOf(d)));
	}
	
	/**
	 * 四则运算
	 */
	@Test
	public void compute() {
		// 四舍五入，保留3为小数
		BigDecimal b = BigDecimal.ONE.add(new BigDecimal(10)).divide(new BigDecimal("-3"), 3, BigDecimal.ROUND_HALF_UP).abs();
		System.out.println(b);
	}
	
	@Test
	public void scale() {
		BigDecimal b = new BigDecimal("1.555");
		System.out.println(b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue());
	}
}
