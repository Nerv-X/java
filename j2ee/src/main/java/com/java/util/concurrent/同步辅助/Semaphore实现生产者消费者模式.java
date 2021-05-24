package com.java.util.concurrent.同步辅助;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;


/**
 * Semaphore实现生产者/消费者模式，并限制并发线程数（生产者及消费者数量）
 * 
 * 多进路-单处理-多出路：
 * 		semaphore允许多个线程同时处理任务
 * 		lock保证执行任务的顺序是同步到即单处理
 * @author Nerv
 *
 */
public class Semaphore实现生产者消费者模式 {

	private volatile Semaphore setSemaphore = new Semaphore(10);	// 生产者
	private volatile Semaphore getSemaphore = new Semaphore(20);	// 消费者
	private volatile ReentrantLock lock = new ReentrantLock();
	private volatile Condition setCondition = lock.newCondition();
	private volatile Condition getCondition = lock.newCondition();
	// 商品池，最多4个
	private final int MAX_SIZE = 4;
	private volatile List<Object> list = new ArrayList<>(MAX_SIZE);
	
	/**
	 * 生产
	 */
	public void set() {
		try {
			// 允许最多同时10个生产者执行
			setSemaphore.acquire();
			lock.lock();
			while (list.size() == MAX_SIZE) {
				setCondition.await();	// 生产者等待
			}
			list.add("商品");
			System.out.println(Thread.currentThread().getName() + " 生产力商品");
			getCondition.signalAll();
			lock.unlock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			setSemaphore.release();
		}
	}
	
	/**
	 * 消费
	 */
	public void get() {
		try {
			// 允许同时最多20个消费者
			getSemaphore.acquire();
			lock.lock();
			while (list.size() == 0) {
				getCondition.await();	// 消费者等待
			}
			list.remove(0);
			System.out.println(Thread.currentThread().getName() + " 消费了商品");
			setCondition.signalAll();
			lock.unlock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			getSemaphore.release();
		}
	}
	
	public static void main(String[] args) {
		Semaphore实现生产者消费者模式 service = new Semaphore实现生产者消费者模式();
		IntStream.range(0, 60).forEach(i -> {
			new Thread(() -> {
				service.set();
			}, "ThreadP-"+i).start();
			new Thread(() -> {
				service.get();
			}, "ThreadC-"+i).start();
		});
	}
}
