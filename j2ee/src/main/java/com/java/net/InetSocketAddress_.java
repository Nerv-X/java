package com.java.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Test;

/**
 * InetSocketAddress 代表Socket地址，包括IP地址和端口号，没有任何协议的附件
 * 
 * 	构造方法
 * 		InetSocketAddress(int port) 	port=0，则随机挑选空闲端口
 * 		InetSocketAddress(InetAddress addr, int port) 根据IP地址和端口号创建socket地址
 * 		InetSocketAddress(String hostname, int port)	根据主机名和端口号创建socket地址
 * 
 * 与InetAddress区别：InetSocketAddress不基于任何协议
 * 
 * @author Nerv
 *
 */
public class InetSocketAddress_ {

	/**
	 * 实例方法
	 */
	@Test
	public void test1() {
		InetSocketAddress addr = new InetSocketAddress(8088);
		// 如果地址是字面IP地址创建的，则本方法可能触发名称服务反向查找，即利用DSN服务通过IP找到域名
		System.out.println("主机名：" + addr.getHostName());
		// 如果没有主机名则返回IP地址，不尝试反向查找。如果之前执行getHostName()时经过了DSN解析，则返回的hostName可能是域名
		System.out.println("主机名或地址的字符串形式：" + addr.getHostString());
		InetAddress ip = addr.getAddress();
		System.out.println("ip地址：" + InetAddress_.getAddress(ip));
		
	}
	
	/**
	 * isUnresolved()：是否未解析的，两种情况返回true
	 * 		1）主机名无法被解析为InetAddress
	 * 		2）createUnresolved()创建未解析的套接字地址
	 * createUnresolved()：根据主机名和端口号创建未解析的SocketAddress，但不将hostname解析为InetAddress
	 */
	@Test
	public void test2() {
		// hostname可解析
		InetSocketAddress addr = new InetSocketAddress("www.baidu.com", 8088);
		System.out.println(addr.isUnresolved());
		// createUnresolved()不解析hostname
		addr = InetSocketAddress.createUnresolved("www.baidu.com", 8088);
		System.out.println(addr.isUnresolved());
		// hostname无法解析
		addr = new InetSocketAddress("www.xxx.com", 8088);
		System.out.println(addr.isUnresolved());
	}
}
