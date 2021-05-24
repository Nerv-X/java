package com.nio.chinnels;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

import org.junit.Test;



/**
 * FileChannel：读写文件，可阻塞
 * 
 * 除了字节通道的方法外，定义了文件专用操作：
 * 
 * ·以不影响通道当前位置的方式，对文件的绝对位置的字节读写
 * ·将文件的某个区域映射到内存，用于操作大文件
 * ·可锁定文件的某个区域，阻止其它程序访问
 * ·强制对底层存储设备进行文件的更新，确保系统崩溃时不丢失数据
 * ·以一种可被很多操作系统优化为直接从文件系统缓存读写的高速传输方法，将字节在文件与其他通道中传输
 * 
 * 
 * FileChannel与InputStream/OutputStream：
 * 		大部分情况FileChannel效率不会高很多，因为NIO出现的目的是为了解决I/O线程阻塞的问题，NIO非阻塞线程提高了效率
 * 		NIO的非阻塞的特性是与Socket有关的通道进行实现的
 * 
 * @author Nerv
 */
public class FileChannel_ {
	
	/**
	 * FileInputStream、FileOutputStream、RandomAccessFile的getChannel()返回的是连接到相同底层文件的FileChannel
	 * FileChannel对文件的任何修改，此3个对象都能看到；反之亦然
	 * 
	 * fileInputStream.getChannel() 可读
	 * fileOutputStream.getChannel() 可写，如果通过构造FileOutputStream(File, true)创建的对象，则此FileChannel可能处于添加模式，
	 * 		即每次写入操作都会将位置移到文件末尾再写入数据。（在单个原子操作中，是否移动位置和写入数据与系统有关，因此是不确定的）
	 * randomAccessFile.getChannel() r模式：可读；rw模式：可读写
	 * 
	 */
	public void test1() {
		
	}
	
	/**
	 * position(int n)：设置channel在文件的位置。如果n大于文件当前大小，读取返回-1，写入会导致文件扩大且文件末尾与n之间的字节值时未指定的
	 */
	@Test
	public void test9() {
		try (FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel()) {
			ByteBuffer buffer = ByteBuffer.allocate(40);
			long filesize = channel.size();
			System.out.println("修改前文件大小=" + filesize + "\n修改channel.position = " + (filesize + 5));
			channel.position(filesize + 5);
			System.out.println("读取=" +channel.read(buffer) + "字节");
			channel.write(ByteBuffer.wrap("abc".getBytes()));
			channel.read(buffer, filesize);
			System.out.println("写入" + new String(buffer.array()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * write(ByteBuffer src)：将Buffer的remaining写入文件的channel.position处。会改变channel.position
	 * 		实现接口WritableByteChannel.write()
	 * 
	 * transferTo(position, count, destChannel)：从当前channel.position处读取不超过count个字节并写入另一通道WritableByteChannel
	 * 		相当于write()，只是写入另一channel
	 * 		不改变position
	 * @throws Exception
	 */
	@Test
	public void test2() {
		try (FileChannel channel = new FileOutputStream("d:\\a.txt").getChannel()) {
			ByteBuffer buffer = ByteBuffer.wrap("1234567".getBytes());
			System.out.println("channel位置=" + channel.position());
			System.out.println("从位置=" + channel.position() + "写入" + channel.write(buffer) + "字节后，channel位置="+ channel.position());
			// 移动通道在文件的位置，并再次写入
			channel.position(2);	
			buffer = ByteBuffer.wrap("a".getBytes());
			System.out.println("从位置2写入" + channel.write(buffer) + "字节后，channel位置="+ channel.position());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * write(ByteBuffer src)：同步
	 * @throws FileNotFoundException
	 */
	@Test
	public void test3() {
		try(FileChannel channel = new FileOutputStream("d:\\a.txt").getChannel()){
			IntStream.range(0, 10).forEach(i -> {
				// 每次迭代均以两线程操作同一channel输出内容，输出结果不存在某一行同时出现中英文混合的情况
				Thread t1 = new Thread(()-> {
					ByteBuffer buffer = ByteBuffer.wrap("abcdef\r\n".getBytes());
					try {
						channel.write(buffer);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				Thread t2 = new Thread(()-> {
					ByteBuffer buffer = ByteBuffer.wrap("李智大帝万岁\r\n".getBytes());
					try {
						channel.write(buffer);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				t1.start();
				t2.start();
				try {
					t1.join();
					t2.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});	
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
	}
	
	/**
	 * read(ByteBuffer buffer)：从文件的channel.position处开始读数据进buffer.position处，返回读取的字节数
	 * 		当buffer.remaining==0，返回0；当channel.position在文件末尾时，返回-1
	 * 		实现接口ReadableByteChannel.read()
	 * 		同步的
	 * 		当buffer.remaining < 此行数据总字节数时，则读取remaining个字节
	 * 		改变channel.position
	 * 
	 * transferFrom(src, position, count)：从ReadableByteChannel的remaining读取最多count个字节并写入当前channel的position
	 */
	@Test
	public void test4() {
		try(FileChannel channel = new FileInputStream("d:\\a.txt").getChannel()) {
			ByteBuffer buffer = ByteBuffer.allocate(30);
			int length = channel.read(buffer);
			System.out.println("第一次读取" + length + "字节，" + new String(buffer.array(), 0, length));
			length = channel.read(buffer);
			System.out.println("第二次读取" + length + "字节，" + new String(buffer.array(), 0, length));
			length = channel.read(buffer);
			System.out.println("从文件末尾开始读取" + length + "字节");
			channel.position(0);	// 重新读取第一行
			buffer.position(buffer.limit()); // remaining = 0
			length = channel.read(buffer);
			System.out.println("buffer.remaining=0，读取" + length + "字节");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * write(ByteBuffer[] src)：批量写，将每个buffer的remaining写入文件的channel.position
	 * 		实现接口GatheringByteChannel
	 * 		同步的
	 * 等价于write(ByteBuffer[] src, 0, src.length)
	 * 
	 * write(ByteBuffer[] src, int offset, int length)：从src的offset开始取length个buffer写入
	 * 
	 * write(ByteBuffer src, long position)：在文件指定位置写入数据，channel.position不变
	 * 		如果position大于文件的当前大小，则会将文件扩大以容纳新数据，在原文件末尾与position之间的字节值是未指定的
	 */
	@Test
	public void test5() {
		try(FileChannel channel = new FileOutputStream("d:\\a.txt").getChannel()){
			IntStream.range(0, 10).forEach(i -> {
				// 每次迭代均以两线程操作同一channel输出内容，输出结果不存在某一行同时出现中英文混合的情况
				Thread t1 = new Thread(()-> {
					ByteBuffer buffer1 = ByteBuffer.wrap("abcdef\r\n".getBytes());
					ByteBuffer buffer2 = ByteBuffer.wrap("123456\r\n".getBytes());
					try {
						channel.write(new ByteBuffer[] {buffer1, buffer2});
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				Thread t2 = new Thread(()-> {
					ByteBuffer buffer1 = ByteBuffer.wrap("李智大帝万岁\r\n".getBytes());
					ByteBuffer buffer2 = ByteBuffer.wrap("万岁万岁万万岁\r\n".getBytes());
					try {
						channel.write(new ByteBuffer[] {buffer1, buffer2});
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				t1.start();
				t2.start();
				try {	// 确保子线程结束再关闭channel
					t1.join();
					t2.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * read(ByteBuffer[] dst)：批量读取，从文件的channel.position读取字节放入多个ByteBuffer的remaining中
	 * 		实现ScatteringByteChannel接口
	 * 	相当于read(dst, 0, dst.length)
	 * 
	 * read(ByteBuffer[] dst, int offset, int length)：只用dst的offset开始length个buffer的remaining存储
	 * 
	 * read(ByteBuffer dst, long position)：从文件指定的位置读数据
	 * 		如果position大于文件的当前大小，则不读任何字节
	 */
	@Test
	public void test6() {
		try(FileChannel channel = new FileInputStream("d:\\a.txt").getChannel()){
			ByteBuffer b1 = ByteBuffer.allocate(50), b2 = ByteBuffer.allocate(5);
			b1.position(10);
			b1.limit(15);
			channel.read(new ByteBuffer[] {b1, b2});
			System.out.println("=============================");
			System.out.println(new String(b1.array(), 0, b1.limit()));
			System.out.println("=============================");
			System.out.println(new String(b2.array(), 0, b2.limit()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读、写线程用同一channel操作同一文件
	 * 		write(buffer,position)与read(buffer,position)同步排斥
	 * 		两个线程同时读写某一文件，任一时刻都只有一个线程在运行
	 * 
	 * size()：返回channel关联的文件的当前大小
	 */
	@Test
	public void test7() {
		try (FileChannel channel = new RandomAccessFile("e:\\vmware-workstation-full1413.zip", "rw").getChannel()){
			Thread t1 = new Thread(()-> {
				ByteBuffer buffer = ByteBuffer.allocate((int) (1024 * 1024 * 1024 * 1.3));
				try {
					channel.read(buffer, 0);
					System.out.println("t1结束时间：" + System.currentTimeMillis());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			Thread t2 = new Thread(()-> {
				ByteBuffer buffer = ByteBuffer.wrap("李智大帝万岁万万岁".getBytes());
				try {
					channel.write(buffer, channel.size() + 1);
					System.out.println("t2结束时间：" + System.currentTimeMillis());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			System.out.println("开始时间：" + System.currentTimeMillis());
			t1.start();
			t2.start();
			try {	// 确保子线程结束再关闭channel
				t1.join();
				t2.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * force(boolean metaData)：强制将数据写入包含此文件的存储设备；true-更改数据和元数据，false-只更改数据
	 * 		write()时，操作系统将数据放入系统内核的缓存中以减少硬盘的读写次数，force()将操作系统内核缓存的数据写入硬盘
	 * 		Linux使用的glibc库的2.17版本中，底层调用fsync()，无论true/false都会更改元数据
	 * 		影响性能
	 */
	@Test
	public void test8() {
		try (FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel()){
			channel.position(channel.size());
			IntStream.range(0, 10).forEach(i -> {
				try {
					channel.write(ByteBuffer.wrap("abc\r\n".getBytes()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * map()：将channel文件区域直接映射到内存，返回MappedByteBuffer
	 * 		三种模式
	 * 			1）PRIVATE：专用，写入时复制，修改缓冲区不影响文件，会对修改部分创建专用副本，此修改对其它程序的映射到同一文件的MappedByteBuffer不可见
	 * 			2）READ_ONLY：只读，修改缓冲区则ReadOnlyBufferException
	 * 			3）READ_WRITE：读写，修改缓冲区会传播到文件
	 * 		
	 * MappedByteBuffer：直接字节缓冲区，对象被回收之前，映射关系始终有效。关闭channel对映射关系无影响
	 * 		
	 */
	@Test
	public void test10() {
		try (FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel()){
			// buffer.array()  UnsupportedOperationException
			MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 1, 5);
			IntStream.range(buffer.position(), buffer.limit()).forEach(i -> System.out.print(buffer.get()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * open()：以各种模式打开文件
	 * 	模式（StandardOpenOption枚举）
	 * 		CREATE：创建新文件（如果不存在），需要配合WRITE才能真正创建新文件，一般不单独使用
	 * 		CREATE_NEW：文件存在则报错
	 * 		APPEND：追加写入
	 * 		READ：读取
	 * 		TRUNCATE_EXISTING：如果文件已存在且已以WRITE模式打开，则将其长度截取为0；若仅READ则忽略此选项
	 * 		DELETE_ON_CLOSE：执行channel.close()或关闭JVM时删除文件
	 * 		SPARSE：声明文件时稀疏文件（暂时声明了但没写入数据的空间不占用硬盘容量）
	 * 		SYNC：每次对文件内容或元数据的更新都写入底层存储设备，性能降低
	 * 		DSYNC：较SYNC只同步内容，不同步元数据
	 * 
	 * isOpen()：当前通道是否已打开
	 */
	@Test
	public void test11() {
		// create + write 创建新文件
		// write + TRUNCATE_EXISTING 清空文件内容
		// create + write + delete_on_close 创建文件再删除
		// create_new + space + write 创建稀疏文件（不能用create）
		try (FileChannel channel = FileChannel.open(Paths.get("d:\\b.txt"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE,
				StandardOpenOption.SPARSE)){
			// 文件占用硬盘2G，说好的稀疏文件呢？
			channel.position(Integer.MAX_VALUE);
			channel.write(ByteBuffer.wrap("李智大帝万岁".getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * lock(long position, long size, boolean shared)：锁定文件区域从position开始size个字节，是否共享锁
	 * 		无论文件实际内容如何变化，只锁定position-size区域
	 * 	如果锁定期间channel被另一线程关闭，则AsynchronousCloseException
	 * 
	 * lock() <=> lock(0L, Long.MAX_VALUE, false);
	 * 
	 * 进程1：锁定文件区域，多次运行，只有第一个进程持有锁，其它进程阻塞
	 */
	@Test
	public void lock1() {
		try(FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel()) {
			System.out.println("进程1开始");
			channel.lock(1, 2, false);
			System.out.println("进程1锁定文件");
			while(true) {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 进程2：操作被进程1锁定的区域，java.io.IOException: 另一个程序已锁定文件的一部分，进程无法访问。
	 * 	前置：启动进程1
	 * 	说明：FileChannel调用操作系统API实现对文件的操作，操作系统抛出异常给JVM，JVM将异常信息显示在控制台
	 */
	@Test
	public void lock2() {
		try(FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel()) {
			System.out.println("进程2开始");
			ByteBuffer buffer = ByteBuffer.allocate(30);
			channel.read(buffer, 2);	// 读取锁定区域之外则正常
			System.out.println(new String(buffer.array()));
			System.out.println("进程2结束");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 中断状态的线程执行lock()会抛出FileLockInterruptionException
	 */
	@Test
	public void lock3() {
		try(FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel()) {
			Thread t1 = new Thread(() -> {
				IntStream.range(0, 10000).forEach(i -> System.out.println(i));
				System.out.println("线程中断：" + Thread.currentThread().isInterrupted());
				try {
					channel.lock(1, 2, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			t1.start();
			Thread.sleep(10);
			t1.interrupt();	// 标记线程中断状态为true
			t1.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * lock()阻塞的线程执行中断会使调用interrupt()的线程本身阻塞，因为等待锁而阻塞的线程无法被打断
	 * 	前置：启动线程1
	 * 
	 * lock()的线程执行interrupt()，只是标识为中断，不影响线程运行
	 * 	前置：无
	 */
	@Test
	public void lock4() {
		try(FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel()) {
			Thread t1 = new Thread(() -> {
				try {
					channel.lock(1, 2, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("开始执行t1业务逻辑：");
				while(true) {
					
				}
			});
			t1.start();
			Thread.sleep(10);
			System.out.println("线程t1中断：" + t1.isInterrupted());
			t1.interrupt();	// 标记线程中断状态为true，底层调用AbstractInterruptibleChannel.interrupt(Thread target)，最后阻塞在FileChannelImpl中threads.signalAndWait();
			System.out.println("线程t1中断：" + t1.isInterrupted());//主线程执行interrupt()而阻塞，本句代码无法输出
			channel.close();
			t1.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * tryLock(position, size, shared)：不阻塞，如果已经被其他进程上锁则返回null，因为其他原因无法获取锁会抛异常
	 * 
	 * tryLock() <=> tryLock(0L, Long.MAX_VALUE, false);
	 * 
	 * 前置：启动线程1
	 */
	@Test
	public void lock5() {
		try(FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel()) {
			FileLock lock = channel.tryLock();
			System.out.println(lock);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 同一进程的多个线程锁定同一区域则OverlappingFileLockException
	 */
	@Test
	public void lock6() {
		try(FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel()) {
			channel.lock(1,2, true);
			channel.lock(2,3, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
