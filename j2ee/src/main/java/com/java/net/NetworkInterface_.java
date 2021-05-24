package com.java.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.junit.Test;

/**
 * NetworkInterface 网络接口，包括网络接口名称与IP地址列表。提供了访问网卡设备信息的API
 * 		getInetAddresses()：获取IP地址信息
 * 		getInterfaceAddresses()：获取网络接口地址信息
 * 
 * NetworkInterface、InetAddress、nterfaceAddress关系：
 * 	每个NetworkInterface对象包含多个InterfaceAddress（网络接口），每个InterfaceAddress对应一个InetAddress（IP地址）
 * 	
 * NetworkInterface、Socket关系：
 * 	Socket程序设计需要与网络接口进行交互，使用JDK的NetworkInterface类获得网络接口信息
 * 
 * @author Nerv
 *
 */
public class NetworkInterface_ {

	/**
	 * NetworkInterface 网络接口基本信息
	 * 	
	 * 	本地回环地址(Loopback Address)：127开头的地址，范围127.0.0.1~127.255.255.254（去掉0和255），代表设备的本地虚拟接口，看作是永不宕掉的接口
	 * 			作用：未安装物理网卡，但能通过localhost或本地回环地址访问tomcat
	 * 			备注：localhost是域名，要通过hosts文件解析为127.0.0.1才能进行通信
	 * 	MTU：最大传输单元，网络传输最大数据包的大小，影响传输效率
	 * 		大值：数据包数量少，传输快，延迟高
	 * 		小值：数据包数量多，传输慢，延迟低
	 * 	数据包：网络传输的基本传输单位
	 * 	子接口：由物理网络接口设备创建的虚拟网络接口设备（虚拟网卡）
	 * 			Windows不支持虚拟子接口，Linux的虚拟子接口作为物理接口的子接口被创建
	 * 	
	 * 	单播：一对一发送数据
	 * 	组播：一对多个主机发送数据
	 * 	广播：一对网络中所有主机发送数据
	 * @throws SocketException
	 */
	@Test
	public void test1() throws SocketException {
		Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
		while (networkInterface.hasMoreElements()) {
			NetworkInterface each = networkInterface.nextElement();
			if(each.getHardwareAddress() == null) continue;
			// index >= 0，索引未知时-1
			System.out.println("网络设备索引：" + each.getIndex());
			// 网络设备在操作系统中的名称，多数为eth+数字，eth代表以太网，是局域网通用的通信协议标准
			System.out.println("网络设备名称：" + each.getName());
			// 网络设备在操作系统中的显示名称，包括厂商名称和网卡型号等信息
			System.out.println("网络设备显示名称：" + each.getDisplayName());
			// 硬件地址、MAC地址，网卡设备的唯一ID，以十六进制表示，共48位
			// System.out.print("网卡的物理地址：" + each.getHardwareAddress());
			System.out.println("网络设备已开启并运行：" + each.isUp());
			System.out.println("是否本地回环接口：" + each.isLoopback());
			System.out.println("最大传输单元：" + each.getMTU() + "字节");
			System.out.println("是否虚拟的子网络接口：" + each.isVirtual());
			System.out.println("虚拟子接口的父接口：" + each.getParent());
			// 点对点：用来通过拨号或专线方式建立点对点连接以发送数据，一种简单连接的通信方式
			System.out.println("是否点对点设备：" + each.isPointToPoint());
			System.out.println("是否支持组播：" + each.supportsMulticast());
			/*
			 * 虚拟子接口列表
			 */
			Enumeration<NetworkInterface> subInterfaces = each.getSubInterfaces();
			while (subInterfaces.hasMoreElements()) {
				NetworkInterface sub = subInterfaces.nextElement();
				System.out.println("\t子接口显示名称：" + sub.getDisplayName());
			}
			System.out.println();
		}
	}

	/**
	 * 静态方法
	 * 		getByIndex()：根据索引获得NetworkInterface
	 * 		getByName()：根据name获取NetworkInterface
	 * 		getByInetAddress()：根据InetAddress对象获得NetworkInterface，如果此IP地址绑定到多个网络接口，则不确定返回哪个网络接口
	 * 
	 * @throws SocketException 
	 * @throws UnknownHostException 
	 */
	@Test
	public void test2() throws SocketException, UnknownHostException {
		NetworkInterface each = NetworkInterface.getByIndex(1);
		System.out.println("网络设备显示名称：" + each.getDisplayName());
		each = NetworkInterface.getByName("lo");
		System.out.println("网络设备显示名称：" + each.getDisplayName());
		each = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
		System.out.println("网络设备显示名称：" + each.getDisplayName());
	}
}
