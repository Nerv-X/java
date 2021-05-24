package com.java.lang;

import java.lang.Character;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.System;

import org.junit.Test;

/**
 * Character	字符封装类
 * 		在Java中，char类型用UTF-16编码描述一个代码单元，按索引顺序从高位到低位
 * 		字节码文件(.class）采用UTF-8编码
 * 		
 * 	静态方法
 * 		isHighSurrogate(char)		是否增补代码点的高位编码单元（值是否在U+D800～U+DBFF）
 * 		isLowSurrogate(char)		是否增补代码点的低位编码单元（值是否在U+DC00〜U+DFFF）
 * 		isSupplementaryCodePoint(int)	代码点是否为增补代码点
 * 		toChars(int)				代码点数值转char数组
 * 
 * Unicode
 * 		定义了字符与数值的映射规则，没有定义存储方式
 * 	Unicode标准中，代码点采用十六进制书写，并加上前缀U+，例如U+0041（字母A）
 * 	Unicode代码点可以分成17个代码级别（code plane）
 * 		基本的多语言组别		代码点从U+0000到U+FFFF，包含经典的Unicode字符
 * 							U+D800~U+DFFF共2048个码位为空白区域，称为Surrogate
 * 		16个附加级别			代码点从U+10000到U+10FFFF，为增补字符。
 * 	Unicode规范中定义，每一个文件的最前面分别加入一个表示编码顺序的字符，这个字符的名字叫做”零宽度非换行空格”（ZERO WIDTH NO-BREAK SPACE），用FEFF表示
 * 		如果一个文本文件的头两个字节是FE FF，就表示该文件采用大头方式（Big endian）；FF FE则表示小头方式（Little endian）
 * 
 * UTF-16
 * 		Unicode标准的存储实现，用16位表示一个基本级别的字符，32位表示附加级别的字符
 * 	存储方式
 * 		基本级别：每个字符用16位表示，称为一个代码单元。范围U+0000~U+FFFF
 * 		附加级别：每个字符用连续的2个代码单元表示。构成的编码值一定落入基本级别中空闲的2048字节内，
 * 			通常被称为替代区域[U+D800～U+DBFF用于第一个代码单元即high surroagte，U+DC00〜U+DFFF用于第二个代码单元即low surroagte]
 * 
 * UTF-8
 * 		Unicode标准的存储实现，用1-3个字节表示一个基本级别的字符，用4字节表示附加级别的字符
 * 
 * UTF-32
 * 		Unicode标准的存储实现，用4个字节表示一个字符
 * 
 * java.nio.charset.Charset.forName(charsetName)支持的参数（大小写不敏感）
 * 		UTF-16BE		big-endian方式排序的UTF-16
 * 		UTF-16LE		little-endian方式排序的UTF-16
 * 		UNICODE			默认为UTF-16
 * 
 * 参考资料：
 * 	[1] Unicode背景介绍 https://www.cnblogs.com/vinozly/p/5155304.html
 * 	[2] Unicode详解 https://blog.csdn.net/worm0527/article/details/70306125
 * 	[3] UTF-8、UTF-16编码字符所占字节数 https://blog.csdn.net/worm0527/article/details/70833531
 * @author nerv
 *
 */
public class Character_ {
	
	/**
	 * 代码点（字符）的声明方式
	 */
	@Test
	public void test1() {
		char c = '中';	// 字符
		c = '\uD800';	// unicode基本字符表达式
		// unicode增补字符表达式
		char[] supplementaryCodePoint = Character.toChars(Integer.parseInt("1F605", 16));
		c = 0xFFFF;		// 默认字符集UTF-16表达范围内的数值
		System.out.println(new String(supplementaryCodePoint));
		System.out.println(c);
	}
	
	/**
	 * 不同编码占用的字节数
	 * 		java的字节码文件（.class）文件采用的是UTF-8编码，但是在java 运行时会使用UTF-16编码。转码时会在前面“零宽度非换行空格”字符
	 * 		FEFF占用两个字节，所以java环境下UTF-16编码的字符多占了2个字节
	 * @throws Exception
	 */
	@Test
	public void test11() throws Exception {
		// 第二个字符串为2个增补字符
        String[] strArr = {"中", "��", "a", "aa"};
        String[] charsetArr = {"gbk", "gb2312", "unicode", "utf-8", "utf-16", "utf-16BE", "utf-16LE"};
        for(String str : strArr) {
            System.out.println(str);
            for(String charset : charsetArr) {
            	byte[] strByte = str.getBytes(charset);
                System.out.println("编码：" + charset 
                        + "\t所占字节数：" + strByte.length
                        + "\t16进制：" + bytesToHexStr(strByte));
            }
            System.out.println("============================");
        }
	}

	/**
	 * 将byte[]用十六进制字符串
	 * @param bytes
	 * @return
	 */
    private String bytesToHexStr(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < bytes.length; i++) {
        	// 每个字节对应2个16进制数
            builder.append(Integer.toHexString(bytes[i])).append(Integer.toHexString(bytes[i] & 0xF));
        }
        return builder.toString();
    }
	
	
}
