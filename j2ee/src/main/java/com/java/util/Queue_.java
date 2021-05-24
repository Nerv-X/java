package com.java.util;

import java.util.concurrent.*;
import java.util.stream.IntStream;

import org.junit.Test;
/**
 * 架构
 * 	Queue(I)：单向队列，只能操作列首
 * 		PriorityQueue：非并发，有序（元素必须实现Comparable），单向队列
 *		Deque(I)：双向队列
 *			ArrayDeque：非并发
 *			LinkedList：非并发，支持操作列首、列尾，还可根据索引位置操作数据
 *			ConcurrentLinkedDeque：并发，支持操作列首、列尾
 *		ConcurrentLinkedQueue：并发，只能操作列头
 *		BlockingQueue(I)：单向阻塞队列
 *			ArrayBlockingQueue：非并发，有序，有界（必须定义初始容量）
 *			LinkedBlockingQueue：非并发，有序
 *			PriorityBlockingQueue：并发，有序（元素必须实现Comparable）
 *			BlockingDeque(I)：双向阻塞队列
 *				LinkedBlockingDeque：非并发，有序
 *			SynchronousQueue：同步阻塞队列，无容量。存取线程同时进行
 *			DelayQueue：延迟阻塞队列
 *			TransferQueue(I)：类似SynchronousQueue，可阻塞或非阻塞
 *				LinkedTransferQueue
 *
 * 类/接口
 *	Queue(I) 单向队列
 *		poll()：删除列首并返回，空集合返回null
 *		remove()：删除列首并返回，空集合报错
 *	Deque(I) 双向队列
 *		pollFirst()
 *		pollLast()
 *		removeFirst()
 *		removeLast()
 *	BlockingQueue(I) 阻塞队列
 *		put()：阻塞，直到有空余位置
 *		take()：阻塞，直到队列有数据
 *	TransferQueue(I)：类似SynchronousQueue
 *		transfer()：阻塞，类似SynchronousQueue.put()
 *		tryTransfer()：非阻塞，如果有take()阻塞的线程则直接将数据传给对方，否则返回false
 *		hasWaitingConsumer()：是否有消费者在等待数据
 *		getWaitingConsumerCount()：获取等待数据的消费者数量
 *
 * @author Administrator
 *
 */
public class Queue_ {

	/**
	 * ArrayBlockingQueue：有界单向阻塞队列
	 * 注意：add()仍然非阻塞
	 */
	public void test2() {
		ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);
		queue.add(3);
		System.out.println("开始阻塞...");
		try {
			queue.put(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("阻塞结束...");
	}
	
	/**
	 * PriorityBlockingQueue：并发，有序，单向阻塞队列
	 */
	public void test3() {
		// 1.元素必须实现Comparable并排序
		class UserInfo implements Comparable<UserInfo> {
			private int id;
			private String name;
			public UserInfo(int id, String name) {
				this.id = id;
				this.name = name;
			}

			@Override
			public int compareTo(UserInfo o) {
				if (o == null) {
					return 1;
				}
				return this.id - ((UserInfo) o).id;
			}
		}
		BlockingQueue<UserInfo> queue = new PriorityBlockingQueue<>();
		queue.add(new UserInfo(3, "user3"));
		queue.add(new UserInfo(4, "user4"));
		queue.add(new UserInfo(2, "user2"));
		queue.add(new UserInfo(1, "user1"));
		queue.add(new UserInfo(1, "user1a"));
		// 2.输出有序队列
		while (!queue.isEmpty()) {
			UserInfo u = queue.poll();
			System.out.println(u.id + "," + u.name);
		}
	}
	
	/**
	 * SynchronousQueue：同步阻塞队列，无容量
	 * @throws InterruptedException
	 */
	@Test
	public void test4() throws InterruptedException {
		BlockingQueue<Integer> queue = new SynchronousQueue<>();
		// 取数据，线程阻塞。因为是空队列
		new Thread(() -> {
			try {
				System.out.println(queue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		Thread.sleep(2000);
		// 存数据，阻塞的线程继续执行
		new Thread(() -> {
			try {
				queue.put(444);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * DelayQueue：延迟阻塞队列
	 */
	public void test5() {
		TimeUnit NANO = TimeUnit.NANOSECONDS;	// 纳秒
		// 1.元素必须实现Delayed并排序
		class UserInfo implements Delayed {
			private String name;
			private long delayTime;	
			public UserInfo (String name, long delayTime) {
				this.name = name;
				this.delayTime = delayTime;
			}
			@Override
			public int compareTo(Delayed o) {
				if (this.getDelay(NANO) > o.getDelay(NANO)) {
					return 1;
				} else if (this.getDelay(NANO) < o.getDelay(NANO)) {
					return -1;
				}
				return 0;
			}

			@Override
			public long getDelay(TimeUnit unit) {
				return unit.convert(delayTime - System.nanoTime(), NANO);
			}
			
		}
		DelayQueue<UserInfo> queue = new DelayQueue<>();
		queue.put(new UserInfo("name3", 300000));
		queue.put(new UserInfo("name2", 200000));
		queue.put(new UserInfo("name4", 400000));
		queue.put(new UserInfo("name1", 100000));
		IntStream.range(0, queue.size()).forEach(i -> {
			try {
				System.out.println(queue.take().name + " " + System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * TransferQueue(I)：类似SynchronousQueue
	 * @throws InterruptedException 
	 * 
	 */
	public void test6() throws InterruptedException {
		TransferQueue<Integer> queue = new LinkedTransferQueue<>();
		// 取数据，线程阻塞。因为是空队列
		new Thread(() -> {
			try {
				System.out.println(queue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		Thread.sleep(2000);
		// 存数据，阻塞的线程继续执行
		new Thread(() -> {
			try {
				queue.transfer(444);;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public static void main(String[] args) throws Exception {
		new Queue_().test6();
	}
}
