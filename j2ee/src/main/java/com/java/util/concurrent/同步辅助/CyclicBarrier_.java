package com.java.util.concurrent.同步辅助;
/**
 * [同步辅助类]CyclicBarrier
 * 	区别
 * 		CountDownLatch: 一组线程等待另一组线程完成某个事情后才能继续执行；减法计数；计数不能重置
 * 		CyclicBarrier: 一组线程互相等待，必须每个线程都完成某个事情后才能继续执行；加法计数；计数可以重置
 * 	构造
 * 		CyclicBarrier(int parties, Runnable)：每parties个线程都执行await()后则执行Runnable
 * 	实例方法
 * 		getNumberWaiting()：返回当前计数，即已经执行await()的线程数
 * 		getParties()：返回parties数
 * 		
 * @author Nerv
 *
 */

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;

import org.junit.Test;

public class CyclicBarrier_ {

	/**
	 * 每PARTIES个线程执行完同一CyclicBarrier对象的await()才能继续执行
	 * @throws InterruptedException 
	 */
	@Test
	public void test1() throws InterruptedException {
		final int PARTIES = 2;
		// 1.待全部线程执行await()后执行run()
		CyclicBarrier cb = new CyclicBarrier(PARTIES, new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName()+ "：已经有"+PARTIES+"个线程到了");
			}
		});
		// 2.每2个一组，共4组
		IntStream.range(0, 8).forEach(i -> {
			new Thread(() -> {
				try {
					Thread.sleep((int) (Math.random() * 1000));
					System.out.println(Thread.currentThread().getName() + "到了！"+cb.getNumberWaiting());
					cb.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}).start();
		});
		Thread.sleep(10000);
	}
	
	/**
	 * reset()：重置屏障，所有await()的线程报错BrokenBarrierException
	 * @throws InterruptedException
	 */
	@Test
	public void test2() throws InterruptedException {
		// 1）声明屏障3个线程为一组
		CyclicBarrier cb = new CyclicBarrier(3, new Runnable() {
			@Override
			public void run() {
				System.out.println("这句执行不到");
			}
		});
		// 2）2个线程平等待在屏障处
		IntStream.range(0, 2).forEach(i -> {
			new Thread(() -> {
				System.out.println("Thread" + i +"：准备");
				try {
					cb.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
				System.out.println("Thread" + i +"：结束");
			}).start();
		});
		Thread.sleep(2000);
		// 3）重置屏障，则await()的线程报错
		cb.reset();
	}
}
