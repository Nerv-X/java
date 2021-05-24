package com.java.util.concurrent.线程池;
/**
 * CompletionService，以异步形式一边生产新任务，一边处理已完成任务的结果。将执行任务与处理任务分开，解决Future阻塞同步性的问题
 * 		如果希望Future按提交顺序排列则用ThreadPoolExecutor，如果希望按处理完成顺序则用CompletionService
 * 
 * 	submit()：提交任务
 * 	take()：取出最先完成的任务Future，只与完成顺序有关，与提交顺序无关。如果不存在Future则阻塞
 * @author Administrator
 *
 */

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;

public class CompletionService_ {
	
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
	// ExecutorCompletionService是唯一实现类，以线程池对象作为参数
	private CompletionService<String> service = new ExecutorCompletionService<>(executor);
	
	/**
	 * submit()：提交任务
	 * take()：取出最先完成的任务Future，只与完成顺序有关，与提交顺序无关。如果不存在Future则阻塞
	 * poll()：取出最先完成的任务Future，不阻塞。如果不存在Future则返回null
	 * poll(long, timeUnit)：超时则返回null
	 */
	@Test
	public void test1() {
		IntStream.rangeClosed(1, 5).forEach(i -> {
			Callable<String> c = new MyCallable("任务" + i, (6-i) * 1000);
			service.submit(c);
		});
		IntStream.rangeClosed(1, 5).forEach(i -> {
			System.out.println("等待打印第" + i+"个返回值");
			try {
				System.out.println(service.take().get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * 任务异常时：主线程不执行future.get()就不出现异常，出异常时其它全部线程都终止
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void test2() throws InterruptedException, ExecutionException {
		service.submit(() -> {
			System.out.println("开始：" + System.currentTimeMillis());
			int i = 0;
			if (i == 0)
				throw new Exception("这是异常");
			System.out.println("结束：" + System.currentTimeMillis());
			return "zzz";
		});
		service.submit(() -> {
			System.out.println("开始：" + System.currentTimeMillis());
			System.out.println("结束：" + System.currentTimeMillis());
			return "zzz";
		});
		// 没有调用future.get()，所以不出现异常
		System.out.println("A：" + service.take() .get() );
		System.out.println("B：" + service.take() .get() );
		System.out.println(executor.getPoolSize());
	}
	

	// 自定义Callable，封装name属性
	private class MyCallable implements Callable<String> {
		
		private String name;
		private long sleepValue;
		
		public MyCallable(String name, long sleepValue) {
			super();
			this.name = name;
			this.sleepValue = sleepValue;
		}
		
		@Override
		public String call() throws InterruptedException {
			Thread.sleep(sleepValue);
			return name;
		}
	}
}
