package com.java.lang;

import java.lang.Character;
import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuffer;
import java.lang.System;
import java.lang.Throwable;

import org.junit.Test;
/**
 * String 底层为private final char[]，不可变设计（每次赋值都会指向另一个String对象），因为String没有提供修改底层char[]的方法
 * 
 * 	构造方法
 * 		String(char[])						以指定的char数组作为底层数组
 * 		String(char[], offset, count)		从指定char数组的索引offset开始，共count个字符组成字符串
 * 	实例方法
 * 		length()							UTF-16编码表示下的代码单元数量（每个char对应一个代码单元）
 * 		substring(begin, end)				从底层数组截取[begin, end)并创建新字符串
 * 		substring(begin)					<=> substring(begin, length())
 * 		intern()							将字符串对象放入常量池，并返回池内对象的指针。如果常量池已有等值的对象则直接返回其指针
 * 
 * 		codePointCount(begin, end)			代码点数量（每个字符对应一个代码点）
 * 											如果索引范围内只有半个代码点（只有高位代理没有低位）则按一个代码点计算
 * 		offsetByCodePoints(index,offset)	从索引index处开始查找第offset个代码点，并返回其起始位置（即高位代理的索引）
 * @author Administrator
 *
 */
public class String_ {

	public static void main(String[] args) throws Throwable {
		
		System.out.println(System.getProperty("file.encoding", "ISO8859_1"));
		System.out.println(String_.class.getClassLoader());
		System.out.println("1234".substring(1));
	}
	
	/**
	 * 字符串常量池
	 */
	@Test
	public void test0() {
		// 字符串"a","b"在类加载时被放入常量池
		String s = "a";
		System.out.println(s.hashCode());
		s = "b";
		System.out.println(s.hashCode());
		s = "a";
		System.out.println(s.hashCode());
		// 哈希值相同的两个对象不一定是同一对象
		String s2 = new String("a");
		System.out.println(s2.hashCode());
		System.out.println(s == s2);
	}
	
	/**
	 * 遍历代码单元及代码点
	 */
	@Test
	public void test1() {
		final char CHAR_GENERAL = '仙';
		StringBuffer buffer = new StringBuffer();
		// 增补代码点，1D306是辅助平面字符，占2个char
		char[] supplementaryCodePoint = Character.toChars(Integer.parseInt("1D306", 16));
		buffer.append("特殊字符：").append(new String(supplementaryCodePoint))
			.append("\t高位:").append((int)supplementaryCodePoint[0])
			.append("\t低位：").append((int)supplementaryCodePoint[1])
			.append("\n普通字符：").append(CHAR_GENERAL).append("\t").append((int)CHAR_GENERAL);
		// 创建字符串，包含普通字符、增补字符
		String s = new String(new char[] {supplementaryCodePoint[0], supplementaryCodePoint[1], CHAR_GENERAL});

		buffer.append("\n=====遍历代码单元=====\n");
		for (int i=0; i < s.length(); i++) {
			buffer.append((int)s.charAt(i)).append("\t");
		}
		buffer.append("\n=====遍历代码点=====\n");
		for (int i=0; i < s.length(); i++) {
			char c = s.charAt(i);
			// 通过高位代理判断确定是否辅助字符
			buffer.append(Character.isHighSurrogate(c) && i < (s.length() - 1) 
					? new String(new char[] {c, s.charAt(i++ + 1)})
					: new String(new char[] {c}))
				.append("\t");
		}
		buffer.append("\n=====遍历代码点的高位索引=====\n");
		for (int i=0; i < s.codePointCount(0, s.length()); i++) {
			buffer.append(s.offsetByCodePoints(0, i)).append("\t");
		}
		System.out.println(buffer.toString());
		/*
		 * 普通字符：占1个char
		 * 辅助字符：占2个char，小索引为高位代理，大索引为低位代理
		 */
	}
	
	/**
	 * 截取字符串
	 */
	@Test
	public void test2() {
		String s = "abcdefghij";
		System.out.println(s.substring(2, 3));
		System.out.println(s.substring(2));
	}
	
	/**
	 * intern()：将字符串对象放入常量池，并返回池内对象的指针
	 */
	@Test
	public void test3() {
		String s1 = "a";
		String s2 = new String("a");
		System.out.println(s2.intern() == s1);
	}
}
