package com.java.util.concurrent.同步辅助;

import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * [同步辅助类]Semaphore类主要用于控制并发线程的数量，是synchronized的升级版
 * 	
 * 构造
 * 	Semaphore(permits)：同一时间内，最多允许permits个线程执行acquire()和release()之间的代码。
 * 		当permits = 1时，线程同步；> 1则无法保证线程的安全性。（许可permits理解为锁）
 *  Semaphore(permits, isFair)：是否公平锁，Semaphore(permits) == Semaphore(permits, false)
 * 实例方法
 * 	acquire(permits)：每次执行则消耗permits个许可，acquire() == acquire(1)
 *  release(permits)：每次执行则增加permits个许可，release() == release(1)
 *  acquireUninterruptibly(permits)：同aciquire(permits)，但线程等待锁时不会被中断
 *  tryAcquire(permits)：尝试获得许可，获取失败则返回false，不会阻塞
 *  availablePermits()：返回当前剩余许可数
 *  drainPermits()：返回当前剩余许可数，并将其清零
 *  getQueueLength()：等待许可的线程数
 *  hasQueuedLength()：是否有线程在等待许可
 * @author Nerv
 *
 */
public class Semaphore_ {

	/**
	 * Semaphore基本用法
	 */
	@Test
	public void test1() {
		// 同一时间内，最多允许1个线程执行acquire()和release()之间的代码，效果同synchronized
		Semaphore semaphore = new Semaphore(1);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					semaphore.acquire(1); // 使用1个许可，减法操作
					System.out.println(Thread.currentThread().getName()+" 开始");
					Thread.sleep(5000);
					System.out.println(Thread.currentThread().getName()+" 结束");
					semaphore.release();
				} catch (InterruptedException e) {
					System.out.println(Thread.currentThread().getName() + " 被中断");
					semaphore.release(); // 别忘了释放许可
				}
			}
		};
		// 3个线程同步执行runnable
		IntStream.range(0,3).forEach(i -> {
			Thread t = new Thread(r, "Thread" + i);
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * acquire()与release()减增许可数
	 * @throws InterruptedException 
	 */
	@Test
	public void test2() throws InterruptedException {
		// 初始许可数
		Semaphore semaphore = new Semaphore(5);
		semaphore.acquire(5); // 减5许可
		System.out.println(semaphore.availablePermits());
		semaphore.release(6); // 增6许可
		System.out.println(semaphore.availablePermits());
	}
	
	/**
	 * acquireUninterruptibly()，线程等待锁时不会被中断
	 * @throws InterruptedException
	 */
	@Test
	public void test3() throws InterruptedException {
		Semaphore semaphore = new Semaphore(1);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					semaphore.acquire(); // 等待锁时会被中断，acquireUninterruptibly()则不会
					System.out.println(Thread.currentThread().getName()+" 开始");
					Thread.sleep(Integer.MAX_VALUE);
					System.out.println(Thread.currentThread().getName()+" 结束");
					semaphore.release();
				} catch (InterruptedException e) {
					System.out.println(Thread.currentThread().getName() + " 被中断");
					semaphore.release(); // 别忘了释放许可
				}
			}
		};
		// 线程1获取锁
		Thread t1 = new Thread(r, "Thread1");
		t1.start();
		Thread.sleep(100);
		// 在线程2等待锁的过程中将其中断
		Thread t2 = new Thread(r, "Thread2");
		t2.start();
		t2.interrupt();
		t2.join();
		t1.join();
	}
	
	/**
	 * 公平与非公平信号量
	 * 		false：先启动的线程不一定先获得许可
	 * 		true：先启动的线程优先获得许可（仅概率保证，非100%确定）
	 */
	static Semaphore semaphore = new Semaphore(1, false);
	@Test
	public void test4() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(Thread.currentThread().getName()+" 启动了");
					semaphore.acquire(); // 等待锁时会被中断，acquireUninterruptibly()则不会
					System.out.println(Thread.currentThread().getName()+" 获得锁");
					semaphore.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		// 注意观察线程启动的顺序与获得许可的顺序
		for(int i = 0; i < 4; i++) {
			new Thread(r, "Thread" + i).start();
		}
	}
}
