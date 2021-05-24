package com.java.util.concurrent.线程池;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * Executor：线程池顶级接口
 * 		ExecutorService：Executor子接口，添加了很多方法
 * 			ThreadPoolExcutor：实现类，因为实例化需要很多参数所以一般用Executors创建线程池对象
 * Executors：线程池工厂，底层创建ThreadPoolExecutor对象
 * 
 * jdk四种线程池
 * 		接口ExecutorService
 * 			
 * @author Nerv
 *
 */
public class Executor_ {

	/**
	 * newFixedThreadPool：固定数量线程，适用稳定且固定的并发场景，如服务器
	 * 
	 * 	异常：线程异常终止时，创建新线程继续执行
	 *	任务队列：LinkedBlockingQueue，在入队列和出队列时使用的是不同的Lock，意味着不存在互斥关系，在多CPU情况下能在同一时刻既消费，又生产，真正做到并行
	 */
	@Test
	public void test1() {
		// 计算机核数
		int processors = Runtime.getRuntime().availableProcessors();
		// 线程池，每个核心2个线程
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(processors << 1);
		test(fixedThreadPool);
	}
	
	/**
	 * newCachedThreadPool：缓存线程池，适用执行时间比较小的任务
	 * 		初始大小0，最大数量为最大整型。任务提交即执行，若无空闲线程则创建新线程，线程60s无任务则终止
	 *	任务队列：SynchronousQueue，take操作需要put等待，put操作需要take等待
	 * 	注意：如果提交速度远大于处理速度，大量任务会引发性能问题
	 * 
	 */
	@Test
	public void test2() {
		// 最大线程数Integer.MAX_VALUE，任务立即执行不等待
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		test(cachedThreadPool);
	}
	
	/**
	 * newSingleThreadExecutor：单线程的线程池，适用按序执行任务的场景，如日志记录
	 * 		保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
	 * 	任务队列：LinkedBlockingQueue，
	 * 	异常：当线程运行时抛出异常的时候会有新的线程加入线程池替他完成接下来的任务
	 */
	@Test
	public void test3() {
		ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
		test(singleThreadPool);
	}
	
	/**
	 * newScheduledThreadPool：固定数量线程，newFixedThreadPool加强版 可以执行延时任务
	 */
	@Test
	public void test4() {
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
		test(scheduledThreadPool);
	}
	
	/**
	 * 测试线程池，处理100个任务，有/无返回值的各一半
	 * @param threadPool
	 */
	private void test(ExecutorService threadPool) {
		// 无返回值任务
		Runnable voidTask = (() -> {
			System.out.println(Thread.currentThread().getName());
		});
		// 有返回值任务
		Callable<String> valueTask = (() -> {
			return Thread.currentThread().getName();
		});
		IntStream.range(0, 100).forEach(i -> {
			// 1.延迟任务
			if (threadPool instanceof ScheduledExecutorService) {
				ScheduledExecutorService schedulePool = (ScheduledExecutorService) threadPool;
				if (i % 2 == 0) {
					// 1.1 任务无返回值
					schedulePool.schedule(voidTask, 1L, TimeUnit.SECONDS);
				} else {
					// 1.2 任务有返回值
					String result = null;
					try {
						result = schedulePool.schedule(valueTask, 1L, TimeUnit.SECONDS).get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					System.out.println(result);
				}
			} else {
			// 2.普通任务
				if (i % 2 == 0) {
					// 2.1 任务无返回值
					threadPool.execute(voidTask);
				} else {
					// 2.2 任务有返回值
					String result = null;
					try {
						result = threadPool.submit(valueTask).get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					System.out.println(i + "：" + result);
				}
			}
		});
		threadPool.shutdown();
	}
	
	/**
	 * 定制线程工厂
	 * newCachedThreadPool(threadFactory)
	 * newFixedThreadPool(int, threadFactory)
	 * newSingleThreadExecutor(threadFactory)
	 */
	@Test
	public void test5() {
		ThreadFactory factory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "定制池中的线程名称" + Math.random());
			}
		};
		ExecutorService executor = Executors.newCachedThreadPool(factory);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + "执行到了");
			}
		});
	}

}
