package com.nio.chinnels;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;


/**
 * AsynchronousChannel 异步通道，AIO。
 * 		异步原理：多线程实现读/写操作。A线程发起读操作，B线程实现读。读不到数据时B阻塞，A继续执行
 * 	AsynchronousChannel 读写文件的异步通道。与一个线程池关联，任务被提交来处理I/O事件，并发送到使用通道上I/O操作结果的CompletionHandler对象
 * 		通道可以安全地并发调用
 * 		close()		导致所有未完成的异步操作异常AsynchronousCloseException
 * 		lock()		异步，获取此通道文件的独占锁Future
 * 					1）若锁定区域已被其它线程锁定则OverlappingFileLockException
 * 					2）若锁定区域已被其它进程锁定则阻塞，直到锁被释放
 * 		read()		异步读取，future.get()才算完成读取操作，未完成时不能再次读否则ReadPendingException
 * 		write()		异步写入，future.get()才算完成读取操作，未完成时不能再次写否则WritePendingException
 * 
 * 	AsynchronousServerSocketChannel	面向流的侦听套接字的异步通道
 * 		open()		创建对象，已打开、未连接
 * 		connect()	将通道变为已连接的
 * 	
 * 	CompletionHandler 用于处理AsynchronousChannel I/O结果，成功则执行completed()，失败则failed()
 * @author nerv
 *
 */
public class AsynchronousChannel_ {

	private final Integer PORT = 8088;
	private Path path = Paths.get("D:\\文件\\日常.txt");
	
	private AsynchronousFileChannel getChannel() {
		try {
			return AsynchronousFileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.READ);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * lock() 文件锁
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void test1() throws IOException, InterruptedException, ExecutionException {
		AsynchronousFileChannel channel = getChannel();
		System.out.println("lock begin " + System.currentTimeMillis());
		// 获取锁
		Future<FileLock> lockFuture = channel.lock();
		FileLock lock = lockFuture.get();
		// 释放锁
		lock.release();
		channel.close();
	}
	
	/**
	 * lock()区域已被其它线程锁定则OverlappingFileLockException
	 * @throws InterruptedException
	 */
	@Test
	public void test2() throws InterruptedException {
		Thread t1 = new Thread(() -> {
			AsynchronousFileChannel channel = getChannel();
			Future<FileLock> future = channel.lock(0, 3, true);
			try {
				FileLock lock = future.get();
				Thread.sleep(8000);
				lock.release();
				channel.close();
			} catch (InterruptedException | ExecutionException | IOException e) {
				e.printStackTrace();
			}
		});
		
		Thread t2 = new Thread(() -> {
			AsynchronousFileChannel channel = getChannel();
			System.out.println("准备获取future");
			Future<FileLock> future = channel.lock(1, 5, true);
			try {
				System.out.println("准备获取文件锁");
				FileLock lock = future.get();
				System.out.println("已获取文件锁");
				lock.release();
				channel.close();
			} catch (InterruptedException | ExecutionException | IOException e) {
				e.printStackTrace();
			}
		});
		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}
	
	/**
	 * lock()区域已被其它进程锁定则阻塞，直到锁被释放
	 */
	@Test
	public void test3_1() {
		// 先上锁
		AsynchronousFileChannel channel = getChannel();
		Future<FileLock> future = channel.lock(0, 3, false);
		try {
			FileLock lock = future.get();
			Thread.sleep(8000);
			lock.release();
			channel.close();
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test3_2() {
		AsynchronousFileChannel channel = getChannel();
		System.out.println("准备获取future");
		// 阻塞，等锁
		Future<FileLock> future = channel.lock(1, 5, false);
		try {
			System.out.println("准备获取文件锁");
			FileLock lock = future.get();
			System.out.println("已获取文件锁");
			lock.release();
			channel.close();
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * lock(attachment, handler) 获取不到锁则阻塞等待
	 * @throws IOException
	 */
	@Test
	public void test4() throws IOException {
		AsynchronousFileChannel channel = getChannel();
		channel.close();
		// 成功获取锁后执行completed()，发生I/O异常则执行failed()
		channel.lock("这是附加内容", new CompletionHandler<FileLock, String>() {
			
			@Override
			public void completed(FileLock result, String attachment) {
				try {
					System.out.println("attachment=" + attachment);
					result.release();
					System.out.println("释放锁");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}

			@Override
			public void failed(Throwable exc, String attachment) {
				System.out.println(exc.getClass().getName());
			}
			
		});
	}
	
	/**
	 * read(buffer, pos)  异步，从文件指定位置读取数据到缓冲区。Future返回读取的字节数，pos超过文件长度则返回-1
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void test5() throws IOException, InterruptedException, ExecutionException {
		AsynchronousFileChannel channel = getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(6);
		Future<Integer> future = channel.read(buffer, 0);
		System.out.println("length=" + future.get());
		channel.close();
		System.out.println(new String(buffer.array()));
	}
	
	/**
	 * write(buffer, pos)：异步，从文件指定位置开始写入
	 * @throws IOException
	 */
	@Test
	public void test6() throws IOException {
		AsynchronousFileChannel channel = getChannel();
		ByteBuffer buffer = ByteBuffer.wrap("李智大帝万岁".getBytes());
		channel.write(buffer, channel.size(), "此乃附加数据", new CompletionHandler<Integer, String>() {

			@Override
			public void completed(Integer result, String attachment) {
				System.out.println("result="+result + "，attachment=" + attachment);
			}

			@Override
			public void failed(Throwable exc, String attachment) {
				System.out.println(exc.getClass().getName());
			}
		});
		channel.close();
	}
	
	/**
	 * AsynchronousServerSocketChannel 异步接收Socket连接
	 * @throws IOException
	 */
	@Test
	public void test7_1() throws IOException {
		AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(8088));
		// 接收完成则执行completed()，出错则failed()
		serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel result, Void attachment) {
				try {
					serverChannel.accept(null, this);
					// 一个main的子线程处理
					// 多线程实现读/写操作。A线程发起读操作，B线程实现读。读不到数据时B阻塞，A继续执行。此之谓异步
					System.out.println("已接收 " + Thread.currentThread().getName());
					ByteBuffer buffer = ByteBuffer.allocate(20);
					Future<Integer> readFuture = result.read(buffer);
					System.out.println(new String(buffer.array(), 0, readFuture.get()));
					result.close();
				} catch (InterruptedException | ExecutionException | IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				System.out.println(exc.getClass().getName());
			}
		});
		while(true) {}
	}
	
	@Test
	public void test7_2() throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", PORT);
		OutputStream out = socket.getOutputStream();
		out.write("李智大帝万岁！".getBytes());
		out.flush();
		out.close();
		socket.close();
	}
	
	/**
	 * 重复读/重复写异常
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void test8() throws IOException, InterruptedException, ExecutionException {
		// 服务端
		AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
		AsynchronousSocketChannel clientChannel = serverChannel.accept().get();
		ByteBuffer buffer = ByteBuffer.allocate(20);
		Future<Integer> future1 = clientChannel.read(buffer);
		// future.get()才算完成读取操作，所以ReadPendingException。可以写
		Future<Integer> future2 = clientChannel.read(buffer);
		// 测试时，用main()执行test7_2()内容作为客户端
	}
	
	
	@Test
	public void test81() throws IOException {
		AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
		serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel result, Void attachment) {
				serverChannel.accept(null, this);
				System.out.println("已接收");
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				System.out.println(exc.getClass().getName());
			}
		});
	}
	
	
	
}
