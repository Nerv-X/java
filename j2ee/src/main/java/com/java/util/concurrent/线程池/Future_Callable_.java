package com.java.util.concurrent.线程池;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
/**
 * Future和Callable获取线程的返回值
 * 
 * Callable
 * 	区别
 * 		callable.call()：有返回值，可声明抛出异常
 * 		runnable.run()：无返回值，不可声明抛出异常
 * 
 * Future
 * 	实例方法
 * 		get()：阻塞，直到call()执行完毕。DiscardPolicy，DiscardOldestPolicy抛弃的任务执行get()会永久阻塞
 * 		isDone()：是否已完成任务
 * 
 * ThreadPoolExecutor
 * 		execute(runnable)：执行任务，无返回值
 * 		submit(runnable)：执行无返回值的任务，返回Future
 * 		submit(callable)：执行有返回值的任务，返回Future
 * 
 * @author Administrator
 *
 */
public class Future_Callable_ {

	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
	
	/**
	 * get()：阻塞，直到call()执行完毕
	 * get(long, timeUnit)：最大时间内等待返回值，超时报错
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void test1() throws InterruptedException, ExecutionException {
		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws InterruptedException {
				Thread.sleep(5000);
				return "李智大帝万岁";
			}
		};
		Future<String> future = executor.submit(callable);
		System.out.println("main A：" + System.currentTimeMillis());
		System.out.println(future.get());
		System.out.println("main B：" + System.currentTimeMillis());
	}
	
	/**
	 * isDone()：任务是否完成
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void test2() throws InterruptedException, ExecutionException {
		Runnable runnable = () -> System.out.println("任务执行中");
		Future<?> future = executor.submit(runnable);
		System.out.println(future.get() + " " + future.isDone());
	}
	
	/**
	 * cancel(boolean)：取消任务，如果正在执行是否中断，返回取消命令是否执行（不保证执行结果）
	 * isCancel()：是否已取消
	 * 	情况一：注释①，任务已结束返回2个false
	 * 	情况二：注释②，任务未开始返回2个true
	 * 	情况三：②改100ms，任务线程在sleep中被中断，线程中断但不报错
	 * 	情况四：注释④，任务线程执行interrupt()设置中断标识但不中断
	 * 	情况五：不注释，③参数true中断；false则不中断
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void test3() throws InterruptedException, ExecutionException {
		Future<String> future = executor.submit(() -> {
			// ①
			Thread.sleep(300);
			while(true) {
				// ④
				if(Thread.currentThread().isInterrupted()) {
					System.out.println("线程中断");
					break;
				}
				System.out.println("执行到了");
			}
			return "";
		});
		// ②
		Thread.sleep(500);
		// ③
		System.out.println(future.cancel(true) + " " + future.isCancelled());
		// junit主线程结束则子线程也会结束
		Thread.sleep(5000);
	}
	
	/**
	 * submit()正确使用方式，要在所有任务submit()后再取数据
	 */
	@Test
	public void test12() {
		Callable<String> c = () -> {
			return new String("你好");//test1();
		};
		ExecutorService executor = Executors.newFixedThreadPool(20);
		// 串行，要等到关闭流之后再取数据
		IntStream.range(0, 10).mapToObj(i -> executor.submit(c)).forEach(t -> {
			try {
				System.out.println(t.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
		//并行
		IntStream.range(0, 10).mapToObj(i -> executor.submit(c)).collect(Collectors.toList()).forEach(t -> {
			try {
				System.out.println(t.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
	}
}
