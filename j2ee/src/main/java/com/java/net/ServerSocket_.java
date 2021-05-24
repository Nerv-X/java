package com.java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

import com.my.entity.User;

/**
 * ServerSocket，用于搭建Socket的服务端环境，而Socket用于Server和Client进行通信
 * 
 * 	方法
 * 		accept()	侦听并接收socket的连接，阻塞直到有客户端socket连接
 * 		setSoTimeout(timeout)	设置accept()阻塞时间（ms），超时则SocketTimeoutException
 * 			·超时虽异常但server仍然有效，配合try-catch可继续accept()
 * 			·timeout=0时永远等待
 * 
 * 服务端与客户端
 * 		1. 客户端在服务端在new ServerSocket()前连接，则客户端ConnectException: Connection refused: connect
 * 		2. 客户端在服务端server.accept()前连接，连接会放到服务端操作系统的队列中，当accept()时从队列中取socket，正常执行
 * @author Nerv
 *
 */
public class ServerSocket_ {

	/**
	 * 构造方法
	 * 	ServerSocket(int port, int backlog, InetAddress bindAddr)
	 * 		backlog：操作系统的socket连接队列最大数量，此限制由操作系统处理
	 * 			·如果<=0，则使用默认值50
	 * 			·如果值过大，则使用系统允许的最大值。win7最大200
	 * 		bindAddr：可在多宿主主机（一台机器有多个IP地址）上使用，ServerSocket仅接受其多个IP地址的其中一个的连接请求
	 * 			·null则为InetAddress.anyLocalAddress()，可使用服务端任意IP地址连接到ServerSocket
	 * 			·客户端new Socket(InetAddress address, int port)的address要与服务端的bindAddr的IP地址相同
	 * ServerSocket(port) 
	 * 		<=> ServerSocket(port, 50) 
	 * 		<=> ServerSocket(port, 50, null)
	 * 		<=> new ServerSocket(); server.bind(new InetSocketAddress(bindAddr, port), backlog);
	 * 
	 * @throws Exception
	 */
	@Test
	public void test1_1() throws Exception {
		ServerSocket server = new ServerSocket(8088, 3, InetAddress.getLocalHost());
		// 确保客户端的连接在accept()前放入操作系统的连接队列
		Thread.sleep(5000);
		while (true) {
			try (
					// 系统的连接队列只有3个socket，所以只能取3个
					Socket client = server.accept();
					ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			) {
					System.out.println(System.currentTimeMillis() + "服务端接收：" + in.readObject());
					client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void test1_2() {
		try {
			// 服务端系统的连接队列只能存3个连接，其它的会被拒绝
			for (int i=0; i < 5; i++) {
				// 从第4个开始被服务端拒绝 ConnectionException: Connection refused: connect
				Socket socket = new Socket(InetAddress.getLocalHost(), 8088);
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(new User(i, "客户端用户" + i));	// 非阻塞
				System.out.println(System.currentTimeMillis() + "：" + i);
				socket.close();
			}
		} catch (Exception e) {
			System.out.println(System.currentTimeMillis());
			e.printStackTrace();
		}
	}
	
	/**
	 * 实例方法
	 * @throws IOException
	 */
	@Test
	public void test2() throws IOException {
		// 等价于new ServerSocket(8088)
		ServerSocket server = new ServerSocket();
		System.out.println("默认是否可复用端口：" + server.getReuseAddress());
		// 设置SO_REUSEADDR，是否可复用端口，允许新的ServerSocket绑定到与旧的ServerSocket同一端口上，默认值与操作系统有关
		// ·必须在绑定之前设置才生效
		// ·当ServerSocket关闭时，如果网络上还有发送到这个serversocket上的数据，这个ServerSocket不会立即释放本地端口，而是等待一段时间，确保接收到了网络上发送过来的延迟数据，然后再释放端口。
		// ·两个共用同一个端口的进程必须都调用serverSocket.setReuseAddress(true)方法才能实现复用
		server.setReuseAddress(false);
		System.out.println("接收缓冲区大小：" + server.getReceiveBufferSize());
		// 设置接收的全部socket的SO_RCVBUF，即socket接收缓冲区大小，服务端要在bind()前设置，客户端要在connect()前设置
		server.setReceiveBufferSize(66);
		System.out.println("是否已绑定：" + server.isBound());
		// ServerSocket绑定Socket地址
		server.bind(new InetSocketAddress((InetAddress)null, 8088), 50);
		System.out.println("是否已绑定：" + server.isBound());
		// 绑定的Socket地址
		System.out.println("关联的SocketAddress对象：" + server.getLocalSocketAddress());
		// 如果未绑定，则getInetAddress()返回null
		System.out.println("绑定的IP地址：" + server.getInetAddress().getHostAddress());
		System.out.println("绑定的端口：" + server.getLocalPort());
		server.close();	// 在accept()中所有阻塞的线程都会抛出SocketException，如果有关联通道，则关闭通道
		System.out.println("是否已关闭：" + server.isClosed());
	}
}
