package com.nio.chinnels;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * Selector 选择器，NIO核心，使用1个线程操作多个通道（I/O多路复用），并发安全
 * 
 * 原理
 * 		每注册1023个通道就创建1个新线程，线程监测Windows的select()方法来监测系统socket事件，若发生事件则通知应用层中的main线程
 * 终止阻塞，继续运行处理事件
 * 		I/O多路复用时，线程不是以for循环方式判断每个通道是否有数据要处理，而是以操作系统底层作为通知器，通知JVM中的线程处理指定的通道
 * 		阻塞与非阻塞模式，具体执行逻辑一致，只是非阻塞模式会立即返回，不等待
 * 架构
 * 	Selector 选择器（多路复用器）
 * 		open()			创建Selector对象
 * 		keys()			注册的键集，返回Set集合keys
 * 						不可直接修改，仅在已取消某个键且已注销其通道后才移除该键
 * 		selectedKeys()	已就绪的键集，keys()子集，返回Set集合selectedKeys
 * 		select()		将keys中通道已就绪的对应端口的键放入selectedKeys中。阻塞，直到监听到客户端发来的事件（即SelectableChannel.register()注册的事件）
 * 						返回值为添加到selectedKeys的键的数量
 * 						处理完客户端后，需要手动将键从selectedKeys中删除，否则遗留到下一次select()
 * 		selectNow()		非阻塞
 * 		select(long)	最长阻塞时间，0则无限等待
 * 						当执行wakeup()、当前线程已中断、阻塞时间到期时，停止阻塞
 * 		wakeup()		使尚未返回的第一个select()操作立即返回
 * 						1）若另一线程阻塞于select()/select(long)则立即返回
 * 						2）若当前未执行select()且未同时调用selectNow()，下一次选择操作将立即返回
 * 		close()			删除全部键并注销通道。关闭后除了close()、wakeup()外所有方法都会ClosedSelectorException
 * 						若有线程中断于select()，则中断该线程
 * 		isOpen()		判断是否开启
 * 		provider()		返回创建此通道的服务提供者
 * 	SelectableChannel 可注册进选择器的通道
 * 	SelectionKey	标识SelectableChannel已经向Selector注册
 * 
 * @author nerv
 *
 */
public class Selector_ {
	
	
	private Runnable server, client;
	
	private static Selector sel; 
	static {
		try {
			sel = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * select()的阻塞效果
	 * @throws InterruptedException
	 */
	@Test
	public void test1() throws InterruptedException {
		server = () -> {
			try {
				SelectableChannel_.getServer(sel);
				System.out.println("开始选择...");
				int keyCount = sel.select();	// 阻塞，直到接收ACCEPT事件
				System.out.println("结束，keyCount=" + keyCount);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		
		client = () -> {
			SelectableChannel_.socketConnect();
		};
		// 服务端select()阻塞3秒，客户端发起socket连接使服务端继续执行
		start(3000, server, client);
	}
	
	
	
	/**
	 * select()出现不阻塞的情况及避免
	 * 		ServerSocketChannel接收到事件后必须处理，否则select()判断有未处理的事件而无法阻塞
	 * 		用accept()处理OP_ACCEPT事件
	 * @throws InterruptedException 
	 */
	@Test
	public void test2() throws InterruptedException {
		server = () -> {
			try {
				SelectionKey serverKey = SelectableChannel_.getServer(sel);
				while(true) {
					System.out.println("开始选择...");
					int keyCount = sel.select();	// 阻塞，直到接收ACCEPT事件
					System.out.println("结束，keyCount=" + keyCount);
					// 取出一个已就绪的通道，用于处理事件
					// 因为只注册了一个ServerSocketChannel，所以直接取
					ServerSocketChannel channel = (ServerSocketChannel) sel.selectedKeys().iterator().next().channel();
					channel.accept();	// 处理accept事件，否则select()无法阻塞
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		
		client = () -> {
			SelectableChannel_.socketConnect();
		};
		// 服务端select()阻塞3秒，客户端发起socket连接使服务端继续执行
		start(3000, server, client);
	}
	
	/**
	 * selectedKeys()重复消费
	 * 		Selector封装有两个集合keys、selectedKeys
	 * 		每次select()都会将通道已就绪的对应端口的键放入selectedKeys，所以处理完客户端需要手动删除
	 */
	@Test
	public void test3() {
		server = () -> {
			try {
				// 注册两个服务端通道，用于处理2个对应端口的客户端Socket
				SelectableChannel_.getServer(sel, 7777);
				SelectableChannel_.getServer(sel, 8888);
				while(true) {
					int keyCount = sel.select();
					Iterator<SelectionKey> iterator = sel.selectedKeys().iterator();
					System.out.println("keyCount = " + keyCount + ",size=" + sel.selectedKeys().size());
					while(iterator.hasNext()) {
						ServerSocketChannel serverChannel = (ServerSocketChannel)iterator.next().channel();
						SocketChannel channel = serverChannel.accept();
						if (channel == null) {
							System.out.println("\t打印此消息表明连接8888服务器时，重复消费！取出的是7777关联的SelectionKey对应的SocketChannel，为null");
						}
						System.out.println("\t" + ((InetSocketAddress)serverChannel.getLocalAddress()).getPort()+ "被连接了");
						iterator.remove();	// 防止重复消费！！！！
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		
		client = () -> SelectableChannel_.socketConnect(7777);
		
		Runnable client2 = () -> SelectableChannel_.socketConnect(8888);
		
		start(server, client, client2, ()->SelectableChannel_.socketConnect(7777), ()->SelectableChannel_.socketConnect(7777));
	}
	
	/**
	 * 传输文件
	 * @throws InterruptedException
	 */
	@Test
	public void test4() throws InterruptedException {
		// 服务端线程
		server = () -> {
			try {
				// 选择器
				Selector sel = Selector.open();
				// 服务端通道
				SelectionKey serverKey = SelectableChannel_.getServer(sel);
				while (true) {
					sel.select();	// 阻塞，监听ACCEPT事件
					Iterator<SelectionKey> iterator = sel.selectedKeys().iterator();
					while(iterator.hasNext()) {		// 用已注册到选择器的已就绪状态的通道处理事件
						SelectionKey key = iterator.next();
						iterator.remove();	// 避免重复消费
						if (key.isAcceptable()) {
							SocketChannel clientChannel = ((ServerSocketChannel)serverKey.channel()).accept();
							clientChannel.configureBlocking(false);
							clientChannel.register(sel, SelectionKey.OP_WRITE);
						}
						if (key.isWritable()) {
							SocketChannel channel = (SocketChannel) key.channel();
							FileInputStream fis = new FileInputStream("D:\\项目\\工作日志.txt");
							FileChannel fileChannel = fis.getChannel();
							ByteBuffer byteBuffer = ByteBuffer.allocateDirect(52428800);	// 50M
							while(fileChannel.position() < fileChannel.size()) {
								fileChannel.read(byteBuffer);
								byteBuffer.flip();
								while(byteBuffer.hasRemaining()) {
									channel.write(byteBuffer);
								}
								byteBuffer.clear();
								System.out.println(fileChannel.position() + " " + fileChannel.size());
							}
							System.out.println("结束写操作");
							channel.close();
							fis.close();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		// 客户端
		client = () -> {
			try {
				Selector selector = Selector.open();
				SocketChannel clientChannel = SelectableChannel_.getClient(selector);
				while(true) {
					System.out.println("begin selector");
					if (clientChannel.isOpen()) {
						selector.select();
						System.out.println(" end selector");
						Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
						while(iterator.hasNext()) {
							SelectionKey key = iterator.next();
							iterator.remove();
							if (key.isConnectable()) {
								// 等待连接完成
								while(!clientChannel.finishConnect()) {
								}
								clientChannel.register(selector, SelectionKey.OP_READ);
							}
							if (key.isReadable()) {
								ByteBuffer buffer = ByteBuffer.allocate(50000);
								int readLength = clientChannel.read(buffer);
								buffer.flip();
								long count = 0;
								while (readLength != -1) {
									count += readLength;
									System.out.println("count=" + count + " readeLength=" + readLength);
									readLength = clientChannel.read(buffer);
									buffer.clear();
								}
								System.out.println("读取结束");
								clientChannel.close();
							}
						}
					} else {
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		start(server, client);
	}
	
	/**
	 * 主线程等待子线程执行完毕
	 */
	static void start(Runnable... rs) {
		start(0, rs);
	}
	
	/**
	 * 主线程等待子线程执行完毕，子线程间隔指定时间启动
	 */
	static void start(long time, Runnable... rs) {
		try {
			List<Thread> threads = new ArrayList<>();
			for(Runnable r: rs) {
				Thread t = new Thread(r);
				t.start();
				threads.add(t);
				Thread.sleep(time);
			}
			for(Thread t: threads) {
				t.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
