package com.java.lang;

import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.StackTraceElement;
import java.lang.String;
import java.lang.System;
import java.lang.Thread;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * 创建线程：构造Thread实例
 * 实现线程执行单元：
 * 		1）继承Thread并重写run()
 * 		2）实现Runnable接口的run()，适合有多个线程共享资源时
 * @author LiZhi
 *
 */
public class Thread_ {

	/**
	 * 1.继承Thread：用static实现共享
	 */
	public void test1() {
		MyThread t1 = new MyThread("一号出号机");
		MyThread t2 = new MyThread("二号出号机");
		MyThread t3 = new MyThread("三号出号机");
		MyThread t4 = new MyThread("四号出号机");
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
	/**
	 * 2.实现Runnable：用同一Runnable实例构造不同Thread实例实现共享
	 */
	public void test2() {
		MyRunnable r = new MyRunnable();
		Thread t1 = new Thread(r, "一号窗口");
		Thread t2 = new Thread(r, "二号窗口");
		Thread t3 = new Thread(r, "三号窗口");
		Thread t4 = new Thread(r, "四号窗口");
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
	
	/**
	 * setDaemon()：守护线程
	 * @throws InterruptedException 
	 */
	public void api1() throws InterruptedException {
		/**
		 * 1.若t1为守护线程：main线程结束则退出JVM进程
		 * 2.若t1为非守护线程：main线程结束后t1仍然运行
		 */
		Thread t1 = new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		//设为守护线程，当JVM没有非守护线程运行时则退出JVM进程
		//t1.setDaemon(true);
		t1.start();
		Thread.sleep(2_000L);
		System.out.println("main线程结束");
	}
	
	/**
	 * sleep()：当前线程休眠，线程状态RUNNING->BLOCKED
	 * @throws InterruptedException 
	 */
	public void api2() throws InterruptedException {
		//子线程休眠2s
		new Thread(() -> {
			long start = System.currentTimeMillis();
			sleep(2);
			long end = System.currentTimeMillis();
			System.out.println("子线程休眠毫秒：" + (end - start));
		}).start();
		//主线程休眠2s
		long start = System.currentTimeMillis();
		sleep(2);
		long end = System.currentTimeMillis();
		System.out.println("主线程休眠毫秒：" + (end - start));
	}
	
	/**
	 * yield()：启发式方法，提醒调度器当前线程让出时间片，线程状态从RUNNING转为RANNABLE
	 */
	public void api3() {
		IntStream.range(0, 2).boxed().map(i -> new Thread(() -> {
			if(i == 0)
				Thread.yield();	//如果CPU资源不紧张，CPU调度器会忽略提醒
			System.out.println(i);
		})).forEach(Thread::start);
	}
	
	/**
	 * interrupt()：打断阻塞，抛出InterruptedException
	 */
	public void api4() {
		//sleep()使子线程转阻塞状态60s
		Thread t = new Thread(() -> {
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch(InterruptedException e) {
				System.out.println("be interrupted");
			}
		});
		t.start();
		//打断阻塞
		t.interrupt();
	}
	/**
	 * isInterrupted()：interrupt()会将中断标识改为true
	 */
	public void api5() {
		Thread t = new Thread(() -> {
			while(true);
		});
		t.setDaemon(true);
		t.start();
		System.out.println(t.isInterrupted());
		t.interrupt();	//中断标识变为true
		System.out.println(t.isInterrupted());
	}
	
	/**
	 * isInterrupted()：捕获InterruptedException后会将中断标识复位为false
	 */
	public void api6() {
		Thread t = new Thread(() -> {
			while(true) {
				try {
					TimeUnit.MINUTES.sleep(1);
				} catch(InterruptedException e) {
					//子线程捕获InterruptedException后会将中断标识复位
					System.out.println("isInterrupted：" + Thread.currentThread().isInterrupted());
				}
			}
		});
		t.setDaemon(true);
		t.start();
		t.interrupt();
	}
	
	/**
	 * interrupted()：返回中断标识并复位为false
	 * @throws InterruptedException
	 */
	public void api7() throws InterruptedException {
		Thread t = new Thread(() -> {
			while(true)
				//第一次为true，之后false
				System.out.println(Thread.interrupted());
		});
		t.setDaemon(true);
		t.start();
		t.interrupt();
		TimeUnit.MILLISECONDS.sleep(2);
	}
	
	/**
	 * 中断标识为true的线程，执行可中断方法会立即抛出InterruptedException
	 */
	public void api8() {
		Thread t = Thread.currentThread();
		//中断线程
		Thread.currentThread().interrupt();
		System.out.println("中断标识：" + t.isInterrupted());
		//显示抛出并捕获InterruptedException不会复位中断标识
		try {
			throw new InterruptedException();
		} catch (InterruptedException e1) {
			System.out.println("显示抛出InterruptedException后中断标识：" + t.isInterrupted());
		}
		//中断后再执行可中断方法会立即中断并复位中断标识
		try {
			TimeUnit.MINUTES.sleep(1);
		} catch(InterruptedException e) {
			System.out.println("中断标识：" + t.isInterrupted());
		}
	}
	
	/**
	 * join()：在线程A上执行B.join()，则A阻塞，等B执行完毕后再继续执行
	 */
	public void api9() {
		//两个子线程
		IntStream.range(1, 3).boxed().map(i -> 
			new Thread(() -> {
				for(int j = 1; j < 10; j++)
					System.out.println(Thread.currentThread().getName() + "：" + j);
			}, String.valueOf("线程" + i))
		//启动线程并join()
		).forEach(t -> {
			t.start();
			try {
				t.join();	//在主线程执行则主线程阻塞，等线程t执行完再继续
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		//main线程
		for(int j = 1; j < 10; j++) {
			System.out.println(Thread.currentThread().getName() + "：" + j);
		}
	}
	
	/**
	 * static getAllStackTraces()：返回所有live线程及其线程栈
	 * 线程：ReaderThread RUNNABLE
	 */
	@Test
	public void api10() {
		for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
			Thread t = entry.getKey();
			System.out.println("线程：" + t.getName() + " " + t.getState());
			// 线程栈信息
			for (StackTraceElement element : entry.getValue()) {
				System.out.println("\t" + element);
			}
		}
	}
	
	/**
	 * TimeUnit可代替Thread.sleep()
	 * @param s
	 * @throws InterruptedException
	 */
	private void sleep(long s) {
		try {
			TimeUnit.SECONDS.sleep(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class MyThread extends Thread {
	//静态共享
	private static int index = 1;
	//柜台名称
	private String name;
	
	public MyThread(String name) {
		this.name = name;
	}
	
	/**
	 * 四个柜台取50个号
	 */
	@Override
	public void run() {
		while(index <= 50)
			System.out.println("柜台：" + name + "当前的号码是：" + index++);
	}
}

class MyRunnable implements Runnable {
	//非静态
	private int index = 1;
	
	//synchronized保证线程安全
	@Override
	public synchronized void run() {
		while(index <= 500)
			System.out.println(Thread.currentThread() + "的号码是：" + index++);
	}
	
}
