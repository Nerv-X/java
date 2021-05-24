package com.java.net;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.junit.Test;

import com.my.entity.User;

/**
 * 基于TCP的Socket通信：服务端ServerSocket，客户端Socket
 * 
 * HTTP
 * 		Web开发领域，实现计算机间数据通信的最多的通信协议
 * 		B/S架构使用的数据通信协议
 * 		高层协议，内部封装了很多细节，请求进入Web容器内部要先执行容器的内部代码，再执行业务代码。开发效率高，运行效率低
 * 		
 * HTTP与Socket关系
 * 		HTTP底层使用Socket技术实现
 * 		针对Socket技术的软件项目是在TCP/IP的基础上进行
 * 		Socket是技术，是实现计算机间通信的前提；HTTP是协议，保证计算机能读懂接收的数据
 * 
 * TCP
 * 		长连接的流协议，以流为单位传输数据，发送的数据有顺序性
 * 		连接需要服务端、客户端3次握手确认彼此存在
 * 		
 * UDP
 * 		无连接协议，不存在长连接、短连接
 *	
 * 长连接，连接成功后连续传输数据，数据传输完不自动关闭
 * 		客户端只有第一次需要握手确认，以后直接传输数据，提高效率；但服务端保存多个Socket对象，占用内存
 * 短连接，传输完数据后立即关闭连接，再次传输数据需要重新连接
 * 		每次传输要建立连接，经过3次握手，效率低；服务端无需保存多个Socket对象
 * 
 * 
 * Socket设置
 * 	setSendBufferSize()：提醒设置SO_SNDBUF，即发送缓冲区大小，只是个提示，不一定会设置成功
 * 	getSendBufferSize()：发送缓冲区实际大小
 * 
 * 	setSoLinger(boolean on, int linger)：设置SO_LINGER，秒为单位
 * 		false：默认，close()时立即返回，但底层socket不关闭，直到发送完缓冲区的剩余数据。正常完成4次挥手
 * 		true：close()后会在阻塞不超过linger秒内关闭socket
 * 			linger=0：立即关闭，丢弃发送缓冲区的剩余数据，并发送RST标记给对方
 * 			linger>65535：被赋值65535
 * 			linger<=65535：会在传输完发送缓冲区剩余数据的时间与linger最小值之后关闭连接
 * 	socket.getSoLinger()：返回linger，未开启则返回-1
 * 
 * 	setSoTimeout(int timeout)：设置SO_TIMEOUT，即socketInputSteam.read()最大阻塞时间（毫秒），超过则SocketTimeoutException
 * 	getSoTimeout()：返回SO_TIMEOUT值
 * 
 * 	sendUrgentData (int data)：发送紧急数据。不存储在输出缓冲区，立即向对方发送1个字节的数据
 * 		· write()是将数据写入缓冲区，flush()才真正传输
 * 		· 接收方无法判断数据时OutputStream还是sendUrgentData()发送的
 * 		用途：实现检测网络连接状态的心跳机制。每隔一段时间发送数据，catch到异常则说明网络断开。不需要对方接受数据
 * 	setOOBInline(boolean on)：接收端设置启用/禁用OOBINLINE选项（TCP紧急数据的接收者）
 * 		false - 默认，丢弃TCP紧急数据
 * 		true - 可接收紧急数据，但无法区分普通数据和紧急数据
 * 
 * 	setKeepAlive(boolean on)：设置SO_KEEPALIVE
 * 		false - 默认，当客户端宕机时，服务端并不知道，仍然保存失效的连接
 * 		true - 如果对方在特定时间（由系统内核设置决定）内没有发送任何数据，则向对方发送一个ACK探测包，判断连接是否有效。如果连接失效则关闭socket
 * 		用途：实际开发中不常用，常规方法是单独启动1个线程，轮询嗅探的方式判断连接状态
 * 
 * 	setTrafficClass(0x10)：设置IP服务类型，用于定性地描述服务质量。4种类型可通过'或'运算进行组合
 * 		向IP头设置流量类型，路由器或交换机根据此流量类型进行不同处理，同时必须要硬件设备参与处理
 * 		win7不支持此特性，要在Linux测试
 * 		1）IPTOS_LOWCOST：		0x02，发送成本低
 * 		2）IPTOS_RELIABILITY：	0x04，高可靠性，保证把数据送到目的地
 * 		3）IPTOS_THROUGHPUT：	0x08，最高吞吐量，一次可接收或发送大批量数据
 * 		4）IPTOS_LOWDELAY：		0x10，最小延迟，传输速度快
 * 	getTrafficClass()：获取IP头中的流量类型
 * 
 * @author Nerv
 *
 */
public class Socket_ {	
	
	/**
	 * ServerSocket服务端
	 * 		accept()：监听端口，阻塞直到客户端连接才继续执行
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void test1_1() throws IOException, InterruptedException {
		// 设置服务端Socket端口
		ServerSocket server = new ServerSocket(8088);
		while(true) {
			System.out.println("server阻塞开始");
			Socket client = server.accept();
			System.out.println("server阻塞结束");
			//Thread.sleep(5000);
			client.close();	// 断开连接
		}
		//server.close();	// 关闭服务端
	}
	
	/**
	 * Socket客户端
	 * 	前置：启动Socket服务端
	 * 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws UnknownHostException 
	 */
	@Test
	public void test1_2() throws IOException, InterruptedException {
		System.out.println("客户端连接准备，开始TCP连接的3次握手");
		Socket client = new Socket("localhost", 8088);	// 请求连接，3次握手
		System.out.println("客户端连接结束，开始TCP断开的4次挥手");
		Thread.sleep(1000);
		client.close();	// 断开连接，4次挥手
		while(true);
	}
	
	/**
	 * ServerSocket创建Web服务器：浏览器访问http://localhost:8088
	 * @throws IOException 
	 */
	@Test
	public void test2() throws IOException {
		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(8088);
		while(true) {
			try (
				Socket client = server.accept();
				// 获取客户端发来的数据
				InputStream in = client.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				OutputStream os = client.getOutputStream();
			) {
				String getString = "";
				while (!"".equals(getString = reader.readLine())) {
					System.out.println(getString);
				}
				os.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
				os.write("<html><body><a href='https://hrb2yy02.crcc.cn/openid/login'>Hello World</a></body></html>".getBytes());
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * SocketInputStream
	 * 		read()：阻塞特性，直到读到客户端发来的数据再继续执行
	 * 		close()：会执行socket.close()关闭套接字
	 * SocketOutputStream
	 * 		write()：非阻塞，将数据放入TCP缓冲区即返回，所以write()不报错不代表接收方正确接收到数据
	 */
	@Test
	public void test3_1() {
		try (
			ServerSocket server = new ServerSocket(8088);
			Socket client = server.accept();	// 阻塞，直到客户端连接
			InputStream in = client.getInputStream();
			InputStreamReader reader = new InputStreamReader(in);
		) {
			char[] data = new char[1024];
			int length;
			do {
				length = reader.read(data);	// 阻塞，直到客户端传输完数据
				System.out.println("服务端接收：" + new String(data,0, length));
			} while (length != -1);
			System.out.println("服务端运行结束");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * SocketOutputStream.write()，每调用一次则传输一次数据
	 * 前置：启动4_1
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void test3_2() throws InterruptedException, IOException {
		Socket client = new Socket("localhost", 8088);	// 连接服务端
		OutputStream out = client.getOutputStream();
		out.write("李智大帝万岁".getBytes());	// 向服务端发送数据
		Thread.sleep(1000);
		out.write("万岁万岁万万岁".getBytes());
		Thread.sleep(Integer.MAX_VALUE);	// 暂停客户端，服务端阻塞在read()继续等待读取数据
		out.close();
		client.close();
	}
	
	/**
	 *	Socket传输图片文件【服务端】
	 */
	@Test
	public void test4_1() {
		try (
				ServerSocket server = new ServerSocket(8088);
				FileOutputStream out = new FileOutputStream("d:\\favorite.jpg");
				Socket client = server.accept();
				InputStream in = client.getInputStream();
		) {
			byte[] data = new byte[1024];
			int length;
			do {
				length = in.read(data);
				out.write(data);
			} while (length != -1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Socket传输图片文件【客户端】
	 */
	@Test
	public void test4_2() {
		try (
				FileInputStream in = new FileInputStream("E:\\_resource\\Picture\\图虫-刘二强\\04.jpg");
				Socket client = new Socket("localhost", 8088);
				OutputStream out = client.getOutputStream();
		) {
			byte[] data = new byte[1024];
			int length;
			do {
				length = in.read(data);
				out.write(data);
			} while (length != -1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 线程池 + Socket通信：服务端一个线程处理一个客户端
	 * @throws IOException
	 */
	@Test
	public void test5_1() throws IOException {
		ServerSocket server = new ServerSocket(8088);
		Executor pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 5);	// 每个CPU核心5个线程
		boolean running = true;
		while (running) {
			Socket socket = server.accept();
			pool.execute(new ReadRunnable(socket));
		}
		server.close();
	}
	
	@Test
	public void test5_2() throws IOException {
		Socket socket = new Socket("localhost", 8088);
		OutputStream out = socket.getOutputStream();
		out.write("李智大帝万岁万万岁".getBytes());
		out.close();
		socket.close();
	}
	
	private class ReadRunnable implements Runnable {
		private Socket socket;
		public ReadRunnable(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try (InputStreamReader reader = new InputStreamReader(socket.getInputStream())) {
				char[] data = new char[1024];
				int length = -1;
				while ((length = reader.read(data)) != -1) {
					System.out.println(new String(data, 0, length));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Socket传输对象，注意I/O流声明顺序
	 * 		服务端先获得ObjectInputStream则客户端要先获得ObjectOutputStream
	 * 		服务端先获得ObjectOutputStream则客户端要先获得ObjectInputStream
	 * 	如果客户端、服务端同时获得ObjectInputSteam则双方都会阻塞在new ObjectInputSteam()
	 * @throws IOException
	 */
	@Test
	public void test6_1() throws IOException {
		try (
				ServerSocket server = new ServerSocket(8088);
				Socket client = server.accept();
				InputStream inputStream = client.getInputStream();
				OutputStream outputStream = client.getOutputStream();
				ObjectInputStream in = new ObjectInputStream(inputStream);
				ObjectOutputStream out = new ObjectOutputStream(outputStream);
		) {
			for (int i = 100; i < 105; i++) {
				System.out.println("服务端接收：" + in.readObject());
				out.writeObject(new User(i, "服务端用户" + i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void test6_2() {
		try(
				Socket socket = new Socket("localhost", 8088);
				InputStream inputStream = socket.getInputStream();
				OutputStream outputStream = socket.getOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(outputStream);
				ObjectInputStream in = new ObjectInputStream(inputStream);
		) {
			for (int i=0; i < 5; i++) {
				out.writeObject(new User(i, "客户端用户" + i));
				System.out.println(in.readObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * bind()：将socket绑定到指定的IP地址和端口，此端口是客户端的端口号。必须在connect()之前
	 * 		只能绑定一次，否则SocketException：Already bound
	 * connect(SocketAddress endpoint, int timeout)：连接到指定的IP地址和端口，连接时间超过timeout则SocketTimeoutException
	 * 		timeout=0无限超时，单位毫秒
	 * 		bind()不能用localhost否则超时不生效，因为远程计算机连接不到"localhost"这个地址
	 * 
	 * new Socket("localhost", 8088) <=> new Socket();socket.connect();   直接连接服务端，客户端端口是由算法自动分配的
	 * @throws IOException
	 */
	@Test
	public void test7_2() throws IOException {
		Socket socket = new Socket();
		socket.bind(new InetSocketAddress("192.168.2.143", 7777));	// 用localhost会使connect超时失效
		socket.connect(new InetSocketAddress("1.1.1.1", 8088), 2000);	// 2s超时
		System.out.println("客户端连接成功");
		socket.close();
	}
	
	/**
	 * 实例方法
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void test8_1() throws IOException, InterruptedException {
		// 设置服务端Socket端口
		ServerSocket server = new ServerSocket(8088);
		while(true) {
			Socket socket = server.accept();
			show(socket, "服务端");
			socket.close();	// 断开连接
		}
	}
	
	@Test
	public void test8_2() throws IOException {
		Socket socket = new Socket();
		socket.bind(new InetSocketAddress("localhost", 7777));	// 用localhost会使connect超时失效
		socket.connect(new InetSocketAddress("localhost", 8088), 0);
		show(socket, "客户端");
		// 关闭套接字会关闭其InputStream、OutputStream及关联的通道
		// 所有阻塞于此套接字的IO操作中的线程将抛出SocketException
		socket.close();
	}
	
	private void show(Socket socket, String msg) {
		// 对于服务端，远程是客户端socket；对于客户端，远程是服务端
		System.out.println("============="+msg+"=============");
		System.out.println("远程端口：" + socket.getPort());
		// 未连接则返回null
		System.out.println("远程InetAddress：" + socket.getInetAddress());
		// 未连接则返回null
		System.out.println("远程的InetSocketAddress：" + socket.getRemoteSocketAddress());
		System.out.println("本地端口：" + socket.getLocalPort());
		System.out.println("本地绑定的InetAddress：" + socket.getLocalAddress());
		// InetSocketAddress = InetAddress + port，尚未绑定则返回null
		System.out.println("本地绑定的InetSocketAddress：" + socket.getLocalSocketAddress());
		System.out.println("已绑定：" + socket.isBound());
		System.out.println("已连接：" + socket.isConnected());
		System.out.println("已关闭：" + socket.isClosed());
	}
	
	/**
	 * shutdownInput()：禁用本端输入流，不影响另一端socket
	 * 		将输入流置于流的末尾，read()将返回EOF（文件结束符）
	 * 		发送到InputStream端的任何数据都将在确认后被丢弃
	 * shutdownOutput()：禁用输出流，不影响另一端socket
	 * 		以前写入的数据都将被发送，且后跟TCP的正常连接终止序列
	 * 		执行后继续write()会抛出IOException
	 * isInputShutdown()：判断输入流关闭
	 * isOutputShutdown()：判断输出流关闭
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void test9_1() throws IOException, InterruptedException {
		// 设置服务端Socket端口
		ServerSocket server = new ServerSocket(8088);
		byte[] data = new byte[2];
		while(true) {
			Socket socket = server.accept();
			InputStream in= socket.getInputStream();
			System.out.println("可读数据：" + in.available() + "字节");
			int length;
			while((length = in.read(data)) != -1) {
				System.out.println(new String(data, 0, length));
				socket.shutdownInput();
				System.out.println("输入流已关闭：" + socket.isInputShutdown());
				System.out.println("可读数据：" + in.available() + "字节");
			}
			socket.close();	// 断开连接
		}
	}
	
	@Test
	public void test9_2() throws IOException {
		Socket socket = new Socket("localhost", 8088);
		// socket.shutdownOutput(); 关闭后write()报错
		System.out.println("输出流已关闭：" + socket.isOutputShutdown());
		socket.getOutputStream().write("abcdefg".getBytes());
		socket.close();
	}
	
	/*
	 * Nagle算法：将许多要发送的数据进行本地缓存，以减少发送数据包的个数来提高网络传输效率，降低网络拥塞
	 * 		用于解决小数据包众多造成的网络拥塞
	 * 
	 * 算法原理
	 * 		在未得到确认ACK前，让发送器将数据送到缓存，直到接收到ACK确认或缓存的数据达到阈值（size）再发送
	 * 		阈值=MSS，即最大报文段长度。TCP为了尽量利用网络宽带，所以希望一次发送足够大的数据。MSS是TCP/IP连接建立时，收发双方协商通信时
	 * 		每一个报文段能承载的最大数据长度，计算方式：
	 * 			MSS = MTU - 20字节的TCP报头 - 20字节的IP报头。以太网环境一般为1500-20-20=1460字节
	 * 
	 * 网络拥塞原因：
	 * 		应用程序一次发送1字节数据，网络传输层中的数据包是41字节，包括1字节用户数据+40字节的TCP/IP协议头
	 * 
	 * 算法应用过程：
	 * 		1）客户端socket将数据"123"（A数据块）写入网络中。此时网络是空闲的，即没有未被确认的小段。因此会立即发送到服务端
	 * 		2）客户端socket又将数据"abc"（B数据块）写入。因为A块的ACK未返回，所以A是未被确认的小段，B块不会被发送到服务端
	 * 		3）服务端收到数据A后，不会立即发送ACK，而是延迟一段时间（t），希望在时间t内服务端向客户端发送应答数据，ACK和应答数据
	 * 		一起发送客户端，节省网络开销【TCP的“确认延迟ACK”机制】
	 * 		4）约40ms后，客户端收到A块的ACK，将B发送到服务端
	 * 
	 * 未被确认的小段
	 * 		小段 - 小于MSS的数据块
	 * 		未被确认 - 数据块发出去但没收到对方的ACK
	 * 
	 * BSD系统允许在空闲连接上依次发送n个完整的MSS大小的数据包，然后发送最后剩余的小数据包，其间不再延时等待
	 * 
	 * 算法优点：提高网络吞吐量，提高了宽带利用率，降低网络拥塞
	 * 算法缺点：客户端和服务端通信有延迟，互动实时性低
	 * 适用场景：大包、高延迟，不适用要求交互速度的B/S、C/S架构
	 */
	
	/**
	 * setTcpNoDelay()：启用/禁用TCP_NODELAY，启用或禁用Nagle算法。默认启用
	 * getTcpNoDelay()：启用判断
	 * 
	 * 	配合wireshark测试：
	 * 		情况1：windows使用SocketOutputStream，无论是否开启NoDelay，每次发送Len=1的数据包
	 * 			You can't use TCP as a messaging system, with or without the Nagle algorithm. It is a byte-stream protocol. 
	 * 			参考https://stackoverflow.com/questions/41048549/socket-settcpnodelaytrue-is-not-working
	 * 		情况2：windows使用BufferedOutputStream，数据包Len只与flush()的时机有关，与是否开启NO_DELAY无关
	 * 		情况3：Linux使用SocketOutputStream，windows运行客户端，NO_DELAY设置有效
	 * 			true - 服务端连续发送5000个Len=1的数据包，不等待ACK
	 * 			false - 具体情况见笔记-《网络分析工具》
	 * 
	 * 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * 
	 */
	@Test
	public void test10_1() throws IOException {
		ServerSocket server = new ServerSocket(8088);
		Socket socket = server.accept();
		System.out.println("是否启动了SO_KEEPALIVE：" + socket.getKeepAlive());
		System.out.println("关闭Nagle算法：" + socket.getTcpNoDelay());
		socket.setTcpNoDelay(false); // 启用Nagle算法
		socket.setSendBufferSize(1);
		System.out.println("发送缓冲区大小：" + socket.getSendBufferSize());
		OutputStream out = socket.getOutputStream();
		for (int i = 0; i < 5000; i++) {
			out.write("1".getBytes());
		}
		out.close();
		socket.close();
		server.close();
	}

	@Test
	public void test10_2() throws IOException {
		Socket socket = new Socket("192.168.17.128", 8088);	// Linux服务端地址
		socket.setTcpNoDelay(false);	// 启用Nagel算法
		System.out.println();
		byte[] data = new byte[1];
		InputStream in = socket.getInputStream();
		long begin = System.currentTimeMillis();
		while (in.read(data) != -1) {
			
		}
		System.out.println(System.currentTimeMillis() - begin);
		socket.close();
	}
}
