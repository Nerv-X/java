package com.java.util.concurrent.同步辅助;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * [同步辅助类]CountDownLatch可控制线程执行任务的时机，使多个线程同时执行
 * 		当count > 0时当前线程wait，为0时继续执行
 * 	方法
 * 		await()：线程等待，直到计数=0
 * 		await(long timeout, TimeUnit unit)：线程等待指定时间
 * 		countDown()：计数减1，当减到0时阻塞的线程继续执行
 * 		getCount()：返回当前计数
 * @author Nerv
 *
 */
public class CountDownLatch_ {
	// 1.创建1个计数的CountDownLatch对象
	private CountDownLatch down = new CountDownLatch(1);
	/**
	 * 先await()后countDown()
	 * @throws InterruptedException
	 */
	@Test
	public void test1() throws InterruptedException {
		new Thread(() -> {
			try {
				System.out.println("A");
				// 2.线程等待
				down.await();
				System.out.println("B");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		Thread.sleep(3000);
		// 3.计数减1，变为0，等待的线程继续执行
		System.out.println("X");
		down.countDown();
	}
	
	/**
	 * 10个线程全部就绪后，主线程再继续执行。（主线程阻塞，子线程计数）
	 * 注意：如果是主线程countDown()、子线程await()则可能出现在主线程countDown()之后
	 * 子线程才执行await()的情况
	 * @throws InterruptedException
	 */
	@Test
	public void test2() throws InterruptedException {
		CountDownLatch maxRunner = new CountDownLatch(10);
		IntStream.range(0, Integer.parseInt("" + maxRunner.getCount())).forEach(i -> {
			new Thread(() -> {
				try {
					Thread.sleep(2000);
					// 因为子线程的准备时间不确定，所以不能在子线程await()，否则无法保证先await()后countDown()
					maxRunner.countDown();
					System.out.println(Thread.currentThread().getName()+"已就绪");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}, "线程"+i).start();
		});
		maxRunner.await();
		System.out.println("都到齐了");
	}
}
