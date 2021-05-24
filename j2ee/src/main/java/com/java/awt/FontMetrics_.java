package com.java.awt;

import sun.font.FontDesignMetrics;

import java.awt.Font;
import java.awt.FontMetrics;

import org.junit.Test;

/**
 * FontMetrics	抽象类，字体的属性及度量方法
 * 
 * FontDesignMetrics	sun包对FontMetrics的实现
 * 	概念
 * 		基准线（baseline）	图像上的字体每一行以基准线对齐，此处即y坐标
 * 	实例方法
 * 		getLeading()			行间距，本行最高处到上一行最低处的距离
 * 		getAscent()				baseline至字符最高处的距离，<=> getMaxAscent()
 * 		getDescent()			baseline至字符最低处的距离，<=> getMaxDescent()
 * 		getHeight()				leading + ascent + descent 结果向上取整
 * 		getMaxAdvance()			所有字符中最大宽度的字符的宽度
 * 		charWidth(char)			单个字符宽度
 * 		charsWidth(char[],off,len)	字符数组中从off开始len个字符的宽度
 * @author nerv
 *
 */
public class FontMetrics_ {

	private Font font = new Font("黑体", Font.ITALIC|Font.HANGING_BASELINE, 20);
	FontMetrics metrics = FontDesignMetrics.getMetrics(font);
	/**
	 * 字符串宽度
	 * @return
	 */
	@Test
	public void test1() {
		// 好像。。中文字符宽fontSize，英文字符宽1/2 fontSize
		String s = "李智大帝万岁！ala~";
		System.out.println(metrics.charsWidth(s.toCharArray(), 0, s.length()));
	}
}
