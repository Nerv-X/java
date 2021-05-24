package com.nio.chinnels;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.junit.Test;

/**
 * FileLock：文件锁，操作系统层面的锁，进程间互斥，同一进程的线程间共同持有锁（即多个线程可安全地使用FileLock对象）
 * 
 * 		同一进程的多个线程锁定同一区域则OverlappingFileLockException
 * 
 * 		两种锁：
 * 			1）共享锁：阻止其它进程获取锁定区域的独占锁，所有进程对此区域只读
 * 			2）独占锁：阻止其它进程获取锁定区域的任何锁，只有本进程可对此区域读写
 * 
 * 		多次锁定：
 * 			仅共享锁和共享锁可以重叠
 * 	
 * 		释放锁三种形式：
 * 			1）channel.release();
 * 			2）channel.close();
 * 			3）终止虚拟机进程
 * 
 * 	方法
 * 		isValid()		锁是否有效
 * 		isShared()		个别操作系统不支持共享锁，而以独占锁代替，需要isShared()判断
 * 		overlaps()		指定区域内是否与当前锁的区域重叠（只比较position和size，不能用来判断区域是否已上锁）
 * 		release()		释放锁
 */
public class FileLock_ {

	/**
	 * FileLock API
	 */
	@Test
	public void lock6() {
		try(FileChannel channel = new RandomAccessFile("d:\\a.txt", "rw").getChannel();
				FileChannel channel2 = new RandomAccessFile("d:\\a.txt", "rw").getChannel()) {
			FileLock lock = channel.lock(1, 2, false);
			System.out.println("hashcode：" + lock.hashCode());
			System.out.println(lock.isShared() ? "共享锁" : "独占锁");
			System.out.println("区域是否与当前锁的范围重叠：" + lock.overlaps(2, 3));
			System.out.println("==========关闭channel会释放锁==========");
			channel.close();
			//lock.release();// 解锁
			System.out.println("锁是否有效：" + lock.isValid());
			ByteBuffer buffer = ByteBuffer.allocate(20);
			channel2.read(buffer);
			System.out.println("读取之前锁定的区域：" + new String(buffer.array()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
