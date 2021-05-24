package com.java.net;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.junit.Test;

/**
 * InterfaceAddress 网络接口地址信息，对应唯一InetAddress（IP地址信息）
 * @author Nerv
 *
 */
public class InterfaceAddress_ {

	/**
	 * 实例方法
	 * 		getAddress()：获取网络接口对应的唯一IP地址信息
	 * 		getBroadcast()：获取网络接口的广播地址信息，IPv6无广播地址返回null
	 * @throws SocketException 
	 * 
	 */
	@Test
	public void test1() throws SocketException {
		Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
		while (networkInterface.hasMoreElements()) {
			NetworkInterface each = networkInterface.nextElement();
			if(each.getHardwareAddress() == null) continue;
			System.out.println("网卡显示名称：" + each.getDisplayName());
			/*
			 * 获取网络接口列表
			 */
			for(InterfaceAddress addr : each.getInterfaceAddresses()) {
				System.out.println("\t网络接口对应IP地址：" + InetAddress_.getAddress(addr.getAddress()));
				System.out.println("\t网络接口对应广播地址：" + InetAddress_.getAddress(addr.getBroadcast()));
				System.out.println("\t网络前缀长度：" + addr.getNetworkPrefixLength());
				System.out.println();
			}
		}
	}
}
