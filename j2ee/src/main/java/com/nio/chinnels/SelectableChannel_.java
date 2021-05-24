package com.nio.chinnels;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.junit.Test;

/**
 * SelectableChannel 可注册进选择器的通道
 * 		register(Selector, ops)			将本通道注册进选择器
 * 		close()							关闭通道，隐式取消此通道关联的所有SelectionKey，若已关闭则返回
 * 		Thread.interrupt()				中断线程，隐式取消此通道关联的所有SelectionKey
 * 		isBlocking()					是否阻塞模式，新建时默认阻塞，注册到选择器前必须设置为非阻塞
 * 		isRegistered()					是否已注册到选择器
 * 		configureBlocking()				true阻塞模式，false非阻塞模式
 * 		blockingLock()					获取阻塞锁（用于configureBlocking()和register()防止重复操作）
 * 		keyFor(sel)						获取当前通道注册在指定选择器的SelectionKey对象
 * 		provider()						获取创建此通道的SelectorProvider
 * 		validOps()						支持的操作，非运算再同指定的SelectionKey进行与运算，结果为0则支持此操作
 * 
 * 常用类
 * 	ServerSocketChannel 封装了ServerSocket的可选择通道，继承AbstractSelectableChannel，并实现NetworkChannel
 * 		socket()			获取关联的ServerSocket
 * 		open()				创建ServerSocketChannel对象（已开启，未绑定，阻塞模式）
 * 		isOpen()			是否已开启
 * 		bind()				将关联的ServerSocket绑定到指定地址，可替代socket().bind()；同时指定最大客户端socket连接数，超过则ConnectionException
 * 		accept()			返回SocketChannel通道，此通道使用缓冲区读写数据
 * 								非阻塞模式时，返回客户端连接关联的通道，没有则返回null；阻塞模式时，返回新连接，无则阻塞
 * 
 * 	SocketChannel	封装了Socket的可选择通道
 * 		connect()			建立连接到服务端，阻塞模式立即连接；非阻塞模式会在随后某个时间才发起连接，连接失败则抛异常
 * 							返回是否已建立连接
 * 		finishConnect()		非阻塞模式完成连接，返回是否连接成功。可在非阻塞connect()后判断是否连接完成
 * 		isConnectionPending()	判断此通道上是否正在进行连接
 * 		setOption()			设置SocketOption，要在connect()之前
 * 		open()				创建SocketChannel
 * 		open(InetSocketAddress)	创建SocketChannel并connect()
 * 
 * 	DatagramChannel 实现UDP通信
 * 		bind()				服务端绑定地址
 * 		connect()			客户端连接此通道的socket（到服务端），无connect()则需要send()时指定目标地址
 * 		disconnect()		断开连接
 * 		send()				将数据发送到指定地址
 * 		join()				将通道加入组播地址
 * 		join(source)		将通道加入组播地址且接收指定IP的客户端数据
 * @author nerv
 *
 */
public class SelectableChannel_ {

	private static final int PORT = 8088;
	
	/**
	 * ServerSocketChannel
	 * @throws IOException
	 */
	@Test
	public void test1() throws IOException {
		ServerSocketChannel channel = ServerSocketChannel.open();
		ServerSocket server = channel.socket();
		server.bind(new InetSocketAddress("localhost", 6666));
		Socket socket = server.accept();
		InputStream is = socket.getInputStream();
		byte[] data = new byte[1024];
		is.read(data);
		is.close();
		socket.close();
		server.close();
		channel.close();
	}
	
	/**
	 * validOps()：支持的操作，非运算再同指定的SelectionKey进行与运算，结果为0则支持此操作
	 * @param channel
	 * @param ops
	 * @return
	 */
	public boolean isSupport(SelectableChannel channel, int ops) {
		return (ops & ~channel.validOps()) == 0;
	}
	
	/**
	 * 创建Socket连接到服务端
	 */
	static void socketConnect(int port) {
		try {
			Socket socket = new Socket("localhost", port);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void socketConnect() {
		socketConnect(PORT);
	}
	
	/**
	 * 网络I/O多路复用
	 * 创建ServerSocketChannel并设置、注册
	 * @return
	 */
	static SelectionKey getServer(Selector sel, int port) {
		try {
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			// 无需ServerSocket.bind()
			serverChannel.bind(new InetSocketAddress("localhost", port));
			// 非阻塞模式才能多路复用，否则异常
			serverChannel.configureBlocking(false);		
			return serverChannel.register(sel, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static SelectionKey getServer(Selector sel) {
		return getServer(sel, PORT);
	}
	
	/**
	 * 创建SocketChannel并设置、注册
	 * @return
	 */
	static SocketChannel getClient(Selector sel) {
		SocketChannel clientChannel = null;
		try {
			clientChannel = SocketChannel.open();
			clientChannel.configureBlocking(false);
			clientChannel.connect(new InetSocketAddress("localhost", PORT));
			clientChannel.register(sel, SelectionKey.OP_CONNECT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return clientChannel;
	}
	
	/**
	 * DatagramChannel实现UDP通信
	 * @throws InterruptedException
	 */
	@Test
	public void test2() throws InterruptedException {
		// 客户端写入
		Runnable client = () -> {
			try {
				Selector sel = Selector.open();
				DatagramChannel channel = DatagramChannel.open();
				channel.configureBlocking(false);
				channel.register(sel, SelectionKey.OP_WRITE);
				sel.select();
				Iterator<SelectionKey> iterator = sel.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					if (key.isWritable()) {
						ByteBuffer buffer = ByteBuffer.wrap("李智大帝万岁".getBytes());
						// 指定服务端地址发送
						channel.send(buffer, new InetSocketAddress("localhost", PORT));
						channel.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		
		// 服务端读取
		Runnable server = () -> {
			try {
				Selector sel = Selector.open();
				DatagramChannel channel = DatagramChannel.open();
				channel.bind(new InetSocketAddress("localhost", PORT));
				channel.configureBlocking(false);
				channel.register(sel, SelectionKey.OP_READ);
				while(true) {
					sel.select();
					Iterator<SelectionKey> iterator = sel.selectedKeys().iterator();
					while(iterator.hasNext()) {
						SelectionKey key = iterator.next();
						if (key.isReadable()) {
							channel = (DatagramChannel) key.channel();
							ByteBuffer buffer = ByteBuffer.allocate(1000);
							channel.receive(buffer);
							System.out.println(new String(buffer.array(), 0, buffer.position()));
						}
						iterator.remove();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		Selector_.start(server, client);
	}
	
}
