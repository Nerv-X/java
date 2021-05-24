package com.java.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;
/**
 * 基于UDP实现的Socket通信：服务端、客户端都使用DatagramSocket（数据报套接字），传输的数据存于DatagramPacket
 * 
 * UDP
 * 		无连接的传输层协议，提供不可靠的传输服务。不会对数据包分组、组装、排序
 * 		数据包：前8字节报头信息，其它字节为传输的数据
 * 		延迟小、传输效率高、适用于对可靠性要求不高的程序。如视频、音频
 * 
 * 
 * 视频用乱序的UDP传输，为什么按顺序显示帧？
 * 		答：对于应用层，一条完整的消息才是它的协议数据，而UDP在它下面，下层协议的顺序不影响上层的结果。
 * @author Nerv
 *
 */
public class DatagramSocket_ {

	/**
	 * UDP实现单播
	 * receive(DatagramPacket p)：阻塞，直到接收到数据
	 * 		如果接收的数据比超过DatagramPacket关联的byte[]，则被截断
	 */
	@Test
	public void server1() {
		try (DatagramSocket socket = new DatagramSocket(8888)) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			socket.receive(packet);	// 阻塞
			System.out.println("数据包中数据长度=" + packet.getLength() + "，内容=" + new String(packet.getData(), 0, packet.getLength()));
			System.out.println(new String(packet.getData()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 单播：发送给指定的单IP地址和端口
	 * send(DatagramPacket p)：发送数据报包，DatagramPacket关联的底层byte[]从offset开始持续length长度的数据
	 * 		一个UDP包最大长度2^16-1 = 65535字节，包含20字节的IP协议头，8字节的UDP协议头。用户数据最大字节长度=65535-20-8=65507，超过则报错
	 */
	@Test
	public void client1() {
		try (DatagramSocket socket = new DatagramSocket()) {
			// 放在Linux执行，地址换成服务端IP
			socket.connect(new InetSocketAddress("localhost", 8888));
			byte[] content = "李智大帝万岁万万岁".getBytes();
			// 注意是字节长度，不是字符长度
			DatagramPacket packet = new DatagramPacket(content, content.length);
			socket.send(packet);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * UDP广播：发送给指定广播地址的服务端
	 */
	@Test
	public void client1_2() {
		try (MulticastSocket socket = new MulticastSocket()) {
			// 1）声明为广播
			socket.setBroadcast(true);
			// 2）IP地址必须是服务端的广播地址，参考test0()
			socket.connect(new InetSocketAddress("192.168.17.255", 8888));
			byte[] content = "李智大帝万岁万万岁".getBytes();
			// 注意是字节长度，不是字符长度
			DatagramPacket packet = new DatagramPacket(content, content.length);
			//packet = new DatagramPacket(content, content.length, InetAddress.getByName("255.255.255.255"), 8888);
			socket.send(packet);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 组播：MulticastSocket继承了DatagramSocket，也可以实现单播、广播。
	 * 使用MulticastSocket组播时，所有通信实体将自己的数据报发送到组播IP地址，并使用MulticastSocket接收其他人发送的数据报
	 * 
	 * MulticastSocket扩展方法
	 * 		setInterface()：关联网络接口，用于单系统有多个网络接口的情况。否则组播时会出现wireshark可以抓到组播包，但服务端仍阻塞在receive()
	 * 		getInterface()：查询关联（监听）的网络接口
	 * 		setTimeToLive(int ttl)：设置数据报最多可以跨过多少个网络
	 * 			ttl==0：指定数据报应停留在本地主机；
	 * 			ttl==1：指定数据报发送到本地局域网；（默认）
	 * 			ttl==32：只能发送到本站点的网络上
	 * 			ttl==64：数据报应保留在本地区；
	 * 			ttl==128：数据报应保留在本大洲
	 * 			ttl==255：数据报可发送到所有地方
	 * 		joinGroup()：加入组，接收方必须加入，发送方可以省略
	 * 		leaveGroup()：离开组
	 */
	@Test
	public void server3() {
		try (MulticastSocket socket = new MulticastSocket(8888)) {
			// 加入指定组
			socket.joinGroup(InetAddress.getByName("224.0.0.5"));
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			socket.receive(packet);	// 阻塞
			System.out.println("数据包中数据长度=" + packet.getLength() + "，内容=" + new String(packet.getData(), 0, packet.getLength()));
			System.out.println(new String(packet.getData()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 组播发送方，Linux的服务端无法接收数据，不清楚原因？？？
	 */
	@Test
	public void client3() {
		try (MulticastSocket socket = new MulticastSocket()) {
			byte[] content = "李智大帝万岁万万岁".getBytes();
			socket.joinGroup(InetAddress.getByName("224.0.0.5"));
			//socket.setLoopbackMode(false);	本MulticastSocket发送的数据报被回送到自身
			DatagramPacket packet = new DatagramPacket(content, content.length, InetAddress.getByName("224.0.0.5"), 8888);
			socket.send(packet);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 本机广播地址：客户端必须向下列地址之一发送消息，本机才能接收到
	 * @throws SocketException
	 */
	@Test
	public void test0() throws SocketException {
		Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
		while(ni.hasMoreElements()) {
			NetworkInterface n = ni.nextElement();
			List<InterfaceAddress> list = n.getInterfaceAddresses();
			IntStream.range(0, list.size()).forEach(i -> {
				InetAddress ip = list.get(i).getBroadcast();
				if(ip != null) {
					System.out.println(ip.getHostAddress());
				}
			});
		}
	}
}
