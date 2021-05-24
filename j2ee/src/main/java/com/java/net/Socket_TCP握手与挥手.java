package com.java.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.IntStream;

import org.junit.Test;

/*
 * 用wireshark+npcap捕获test1_1、test1_2完整通信过程，并分析3次握手具体情况
 * 
 * TCP三次握手时机：ServerSocket绑定到地址（例test3_1、test3_2）
 * 
 * 注意点：
 * 		1. 挥手与握手都是客户端引发的，服务端单独关闭连接或彻底关掉服务端都不会发送数据包
 * 		2. 只有客户端断开连接时才会发起4次挥手，此时就算服务端已经关闭、虚拟机进程结束，仍然会从服务端端口发送断开确认数据包，确保完成4次挥手
 * 		3. 服务端不断开连接，当客户端断开连接时，服务端只完成前2次挥手，第3步会发送RST标志给客户端试图重新建立连接
 * 		4. 4次挥手的前提是服务端、客户端都断开连接（socket.close())，无论谁先谁后
 * 
 * 问题
 * 		1. 服务端虚拟机进程结束，客户端此时断开socket连接，为什么还能完成4次挥手？？？
 * 
 * 
 * 四次挥手
 * 		第一次挥手：客户端以FIN标志通知服务端断开连接
 * 		第二次挥手：服务端确认收到信息
 * 		第三次挥手：服务端等待进行中的数据传输完成，再以FIN标志通知客户端可以断开
 * 		第四次挥手：客户端确认收到信息
 * 注意：只有连接的双方都关闭连接才会完成四次挥手，否则RST
 * 
 * RST（重置连接）出现的3种情况
 * 		1.SYN到达目的端口，然而该端口没有正在监听的服务器（单独运行客户端）
 * 		2.单方面关闭本地socket，则本地向远程发送RST告知对方本地已无法接受数据
 * 			情况1：只有A关闭连接
 * 			情况2：A关闭JVM，B关闭连接
 * 			情况3：A关闭ServerSocket，B关闭连接（Socket），因为Socket连接的维持不需要ServerSocket
 * 		3.向已关闭连接的远程机器发送数据，远程机器会发出RST标志表示无法接收数据
 * 			① write()只负责把数据交给TCP发送缓冲区就可以成功返回了，所以不会出错
 * 			② server收到数据后应答一个RST段，表示服务器已经不能接收数据，连接重置
 * 			③ client收到RST段后无法立刻通知应用层，只把这个状态保存在TCP协议层
 * 			④ 再次write()或read()，由于TCP协议层处于RST状态，因此不会发送数据，而是发一个SIGPIPE信号给应用层，SIGPIPE信号的缺省处理是终止程序
 * 			（JVM将其封装为SocketException）
 * 		
 * 
 */
public class Socket_TCP握手与挥手 {
	
	/**
	 * ServerSocket服务端
	 * 	
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void test1_1() throws IOException, InterruptedException {
		// 设置服务端Socket端口
		ServerSocket server = new ServerSocket(8088);
		System.out.println("server阻塞开始");
		Socket client = server.accept();
		System.out.println("server阻塞结束");
		//Thread.sleep(5000);
		
		// 服务端不断开连接，则当客户端断开连接时，服务端会发送RST标志试图重新建立连接
		//while(true);
		client.close();	// 断开连接
		server.close();	// 关闭服务端
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
	 * TCP三次握手时机：ServerSocket绑定到地址，并非accept()
	 * @throws IOException 
	 * 
	 */
	@Test
	public void test3_1() throws IOException {
		ServerSocket server = new ServerSocket(8088);
		while(true);
	}
	
	@Test
	public void test3_2() throws IOException {
		Socket client = new Socket("localhost", 8088);	// 开始握手
		OutputStream out = client.getOutputStream();
		IntStream.range(0, 3).forEach(i -> {
			try {
				out.write("123".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(i+1);
		});
		out.close();
		client.close();
	}
}
