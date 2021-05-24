package com.nio.chinnels;

import java.nio.channels.*;

import org.junit.Test;

/**
 * NIO通道：用于传输数据，底层实现依赖于操作系统，所以Channel设计为接口数据类型，顶层接口Channel继承Closeable
 * 
 * 	架构
 * 		AsynchronousChannel			支持通道异步I/O操作
 * 			AsynchronousByteChannel	支持异步IO，操作单位为字节
 * 		WritableByteChannel			支持对字节的写操作
 * 			GatheringByteChannel	将多个缓冲区的数据写入通道
 * 		ReadableByteChannel			支持对字节的读操作
 * 			ScatteringByteChannel	从通道读取多个字节到多个缓冲区
 * 			ByteChannel					支持对字节的操作（继承ReadableByteChannel 和 WritableByteChannel）
 * 				SeekableByteChannel		在字节通道中维护position
 * 		NetworkChannel				将通道与socket关联，使通道的数据能在Socket上传输
 * 			MulticastChannel		使通道支持Internet Protocol（IP）多播，即将多个主机地址打包成一个group。
 * 									向这个组发送IP报文相当于同时向多个主机传输数据
 * 		InterruptibleChannel		使通道能以异步的方式关闭与中断
 * 			SelectableChannel		I/O多路复用通道，可注册进选择器
 * 			FileChannel				文件通道
 * 	接口
 * 		
 * @author Nerv
 *
 */
public class Channel_ {
	
	
	/**
	 * AsynchronousChannel接口，用于支持通道异步I/O操作，两种方式
	 * 	1.方法：Future<V> operation()
	 * 		提交I/O操作后直接返回Future对象，以后通过此对象取值
	 * 	2.回调：void operation(A attachment, CompletionHandler<V, ? super A> handler)
	 * 
	 * 异步通道线程安全，但不能在一个未完成的I/O操作上再次执行读写
	 * 异步通道支持取消操作，Future接口cancel()，导致等待I/O结果的线程抛出CancellationException
	 * 		取消时，建议废弃I/O操作中使用的所有缓冲区，因为这些数据不完整。再次打开通道时也要避开这些缓冲区
	 */
	@Test
	public void test2() {
		
		/**
		 * 支持异步IO，操作单位为字节
		 * 	上一个read()完成前不能再次read()，否则抛出ReadPendingException;write()同理
		 * 	read()能否与其他类型IO操作同时进行，与通道的类型有关；write()同理
		 * 	ByteBuffer线程非安全，保证只有单线程对其读写
		 */
		AsynchronousByteChannel asynchronousByteChannel;
		
		
	}
	
	/**
	 * ReadableByteChannel 支持对字节的读操作
	 * 	同一时刻，最多一个线程执行read()，其他read线程阻塞
	 * 	read()能否与其他类型IO操作同时进行，与通道的类型有关
	 */
	
	public void test3() {
		
		
	}
	
	/**
	 * WritableByteChannel 支持对字节的写操作
	 * 
	 * 方法
	 * 		int write(ByteBuffer src)：同步的，将src的remaining写入通道的当前位置，返回写入的字节数
	 * 			同一时刻，最多一个线程执行write()，其他write线程阻塞
	 * 			write()能否与其他类型IO操作同时进行，与通道的类型有关
	 * 		long position()：返回此通道的文件位置
	 * 		position(long newPosition)：设置通道的文件位置
	 */
	public void test4() {

		
	}
	
	/**
	 * NetworkChannel 将通道与socket关联，使通道的数据能在Socket上传输
	 * 		bind()：将Socket绑定到本地地址
	 * 		getLocalAddress()：返回绑定到此Socket的SocketAddress对象
	 * 		setOption()：设置Socket
	 * 		getOption()：查询Socket
	 */
	public void test5() {
		
	}

}
