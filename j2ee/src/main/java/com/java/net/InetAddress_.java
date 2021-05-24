package com.java.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * InetAddress IP地址信息，两个子类Inet4Address、Inet6Address分别描述ipv4和ipv6信息
 * 
 * IP：网络协议地址，用于标识网络中的设备
 * 		IPv4：4组8位的二进制数，一般表示为4组十进制数，总数2^32
 * 		IPv6：8组16位的二进制数，一般表示为8组十六进制数（每组4个），总数2^128
 * 网络前缀长度：即子网掩码
 * 		IPv4值：8（255.0.0.0）、16（255.255.0.0）、24（255.255.255.0）
 * 		IPv6值：128（::1/128）、10（fe80::203:baff:fe27:1243/10）
 * 
 * @author Nerv
 *
 */
public class InetAddress_ {
	/**
	 * 查看网络设备的IP信息
	 * @throws SocketException 
	 * @throws UnknownHostException 
	 */
	@Test
	public void test2() throws SocketException {
		Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
		while (networkInterface.hasMoreElements()) {
			NetworkInterface each = networkInterface.nextElement();
			if(each.getHardwareAddress() == null) continue;
			System.out.println("网卡显示名称：" + each.getDisplayName());
			// 硬件地址、MAC地址，网卡设备的唯一ID，以十六进制表示，共48位
			System.out.println("网卡物理地址：" + getAddress(each.getHardwareAddress(), true));

			/*
			 * 绑定到此网络接口的InetAddress列表
			 */
			Enumeration<InetAddress> addrs = each.getInetAddresses();
			while (addrs.hasMoreElements()) {
				InetAddress ip = addrs.nextElement();
				System.out.println("\t完全限定域名：" + ip.getCanonicalHostName());
				System.out.println("\t主机名：" + ip.getHostName());
				System.out.println("\t主机地址：" + ip.getHostAddress());
				System.out.println("\tIP地址：" + getAddress(ip));
				System.out.println("\t类名：" + ip.getClass().getName());
				System.out.println();
			}
		}
	}
	
	/**
	 * 静态方法
	 * 		getLocalHost()：本地主机的IP地址
	 * 		getLoopbackAddress()：本地回环IP地址
	 * 		getByAddress(String host, byte[] addr)：	根据主机名和ip地址获取ip对象
	 * 		anyLocalAddress()：任意本地IP地址，一般是0.0.0.0/0.0.0.0，如果一台主机有多个IP地址则代表任何一个，用于bind()操作
	 * @throws UnknownHostException
	 */
	@Test
	public void test3() throws UnknownHostException {
		System.out.println("=======================本地主机的IP地址=======================");
		// 主机有多个IP则返回第一个，返回全部用getAllByName()
		show(InetAddress.getLocalHost());
		System.out.println("=======================本地回环IP地址=======================");
		show(InetAddress.getLoopbackAddress());
		System.out.println("=======================根据名称获取IP地址=======================");
		show(InetAddress.getByName("DESKTOP-FHM4CM6"));	// 计算机名
		show(InetAddress.getByName("www.baidu.com"));	// 域名
		show(InetAddress.getByName("127.0.0.1"));	// ip地址
		
	}
	
	/**
	 * 普通方法
	 * @param local
	 */
	private void show(InetAddress local) {
		System.out.println("主机别名：" + local.getHostName());
		// 最终输出是完全限定域名还是别名与DSN服务器有关
		System.out.println("主机完全限定域名：" + local.getCanonicalHostName());
		System.out.println("主机IP：" + local.getHostAddress());
		System.out.println("地址：" + getAddress(local));
		System.out.println(local.getClass().getName());
	}
	
	
	/**
	 * 根据InetAddress是IPv4或IPv6对应格式地址
	 * @param ip
	 * @return
	 */
	public static String getAddress(InetAddress ip) {
		if (ip == null) {
			return "null";
		}
		byte[] addr = ip.getAddress();
		if (ip.getClass() == Inet4Address.class) {
			return getAddress(addr, false);
		} else {
			return getAddress(addr, true);
		}
	}
	
	/**
	 * 将ip地址二进制数组转字符串，如果byte < 0 则+256
	 * @param addr
	 * @param hex	十六进制
	 * @return
	 */
	private static String getAddress(byte[] addr, boolean hex) {
		if (addr == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, addr.length).forEach(i -> {
			byte b = addr[i];
			int data = b < 0 ? b + 256 : b;
			sb.append(hex ? Integer.toHexString(data) : data).append(" ");
		});
		return sb.toString();
	}
}
