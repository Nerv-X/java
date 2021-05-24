package com.java.net;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;

import org.junit.Test;

/**
 * 数据报包，用于基于UDP的Socket通信，包含发送方的用户数据及其长度、远程主机的IP地址和端口号
 * 
 * 底层封装了byte[]，当byte[]内容改变时，影响DatagramPacket内容；类似ByteBuffer.wrap(byte[] d)
 * 
 * 方法：
 * 		byte[] getData()：返回数据缓冲区
 * @author Nerv
 *
 */
public class DatagramPacket_ {

	
	/**
	 * setData(buf, offset, length)：关联底层byte[]，发送数据时发送从offset开始持续length长度，如果length + offset > buf.length，报错 
	 * setData(buf) == setData(buf, 0, buf.length)
	 * getOffset()：获取偏移量offset
	 * setLength()：设置长度（发送/接收的字节数），不能大于buf.length - offset
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void test1() throws UnsupportedEncodingException {
		byte[] data = "李智大帝万岁".getBytes();
		// 如果length + offset > data.length，报错 
		DatagramPacket packet = new DatagramPacket(data, data.length);
		// 修改byte[]影响pakcet内容
		data[0]=(byte)1;
		System.out.println(new String(packet.getData(), 0, packet.getLength()));
	}
	
	
}
