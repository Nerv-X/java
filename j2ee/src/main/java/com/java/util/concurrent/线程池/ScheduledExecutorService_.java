package com.java.util.concurrent.线程池;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * jdk计划任务Timer工具类提供了以计时器或计划任务的功能实现按指定时间或时间间隔执行任务，
 * 但Timer以队列而非池来管理线程，所以效率低。
 * ScheduledExecutorService将线程池与定时任务结合，效率高
 * 继承
 * 	ThreadPoolExecutor
 * 实例化
 * 	Executors.newScheduledThreadPool(n) == new ScheduledExecutorService(n)
 * 	Executors.newSingleThreadScheduledExecutor() == new ScheduledExecutorService(1)
 * 新方法
 * 	schedule(callable, long, timeUnit)：指定时间后开始任务，返回ScheduledFuture
 * 	scheduleAtFixedRate(runnable, long initialDelay, long period, TimeUnit unit)：initialDelay后开始任务，开始后period执行下一个任务
 * 	
 * ScheduledThreadPoolExecutor：实现ScheduledExecutorService
 * 	setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean)：shutdown()后已经schedule()的任务是否继续执行，默认true
 * 	setRemoveOnCancelPolicy(true)：任务被取消后从任务队列删除，配合cancel()使用
 * @author Administrator
 *
 */
public class ScheduledExecutorService_ {
	
	private ScheduledExecutorService executor1 = Executors.newSingleThreadScheduledExecutor();
	private ScheduledThreadPoolExecutor executor2 = new ScheduledThreadPoolExecutor(10);
	
	/**
	 * schedule(callable, long, timeUnit)：指定时间后开始任务，返回ScheduledFuture
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void test1() throws InterruptedException, ExecutionException {
		Callable<String> c = () -> {
			System.out.println("A 开始 " + System.currentTimeMillis());
			Thread.sleep(2000);
			System.out.println("A 结束 " + System.currentTimeMillis());
			return "A";
		};
		System.out.println("main开始：" + System.currentTimeMillis());
		ScheduledFuture<String> future = executor1.schedule(c, 3, TimeUnit.SECONDS);
		System.out.println("main返回：" + future.get() + " " + System.currentTimeMillis());
		System.out.println(future.getClass().getName());
	}
	
	/**
	 * scheduleAtFixedRate(runnable,long initialDelay,long period, TimeUnit unit)：initialDelay后开始任务，开始后period执行下一个任务，
	 * 如果此时上个任务没执行完则等待执行完毕后立即开始下一个任务
	 * 	注意：虽然方法有返回值，但无法返回任务结果。future.get()会使主线程阻塞
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void test2() throws InterruptedException, ExecutionException {
		Runnable r = () -> {
			try {
				System.out.println("A 开始 " + System.currentTimeMillis());
				Thread.sleep(4000);
				System.out.println("A 结束 " + System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		System.out.println("main开始：" + System.currentTimeMillis());
		// 1s后开始任务，开始2s后执行下个任务。不要future.get()
		ScheduledFuture<?> future = executor1.scheduleAtFixedRate(r, 1, 2, TimeUnit.SECONDS);
		System.out.println("main返回：" + System.currentTimeMillis() + " ");
	}
	
	/**
	 * remove(runnable)：删除任务队列中的任务，ScheduledFuture不继承Runnable，但其实现类有实现
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void test3() throws InterruptedException, ExecutionException {
		Runnable r1 = () -> System.out.println("任务1");
		Runnable r2 = () -> System.out.println("任务2");
		System.out.println("main开始：" + System.currentTimeMillis());
		// 1s后开始任务，开始2s后执行下个任务。不要用返回值接收结果
		ScheduledFuture<?> future1 = executor2.scheduleAtFixedRate(r1, 0, 2, TimeUnit.SECONDS);
		Thread.sleep(1000);
		ScheduledFuture<?> future2 = executor2.scheduleAtFixedRate(r2, 0, 2, TimeUnit.SECONDS);
		Thread.sleep(5000);
		System.out.println(executor2.remove((Runnable) future2));
		System.out.println("===========删除任务后===========");
		// 获取任务队列
		BlockingQueue<Runnable> queue = executor2.getQueue();
		Iterator<Runnable> iterator = queue.iterator();
		while (iterator.hasNext()) {
			Runnable r = iterator.next();
			System.out.println("队列中：" + r.hashCode());
		}
	}
	/**
	 * setExecuteExistingDelayedTasksAfterShutdownPolicy()：shutdown()后已经schedule()的任务是否继续执行，默认true
	 * 不能用于scheduleAtFixedRate()/scheduleWithFixedDelay()
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void test4() throws InterruptedException, ExecutionException {
		Runnable r = () -> System.out.println("任务1");
		executor2.schedule(r, 0, TimeUnit.SECONDS);
		// 已经提交的任务终止运行，线程销毁
		executor2.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		executor2.shutdown();
	}
	
	/**
	 * setContinueExistingPeriodicTasksAfterShutdownPolicy()：shutdown()后已经scheduleAtFixedRate()/scheduleWithFixedDelay()的任务是否继续执行，默认true
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void test5() throws InterruptedException, ExecutionException {
		Runnable r = () -> System.out.println("任务1");
		executor2.scheduleAtFixedRate(r, 0, 2, TimeUnit.SECONDS);
		executor2.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		executor2.shutdown();
	}
	/**
	 * cancel(boolean)：队列中的任务不再运行
	 * setRemoveOnCancelPolicy(true)：任务被取消后从任务队列删除
	 * @throws InterruptedException
	 */
	public void test6() throws InterruptedException {
		Runnable r = () -> {
			String name = Thread.currentThread().getName();
			try {
				while(true) {
					if (Thread.currentThread().isInterrupted()) {
						throw new InterruptedException(name + "中断了");
					}
					System.out.println(name);
					IntStream.range(0, 123456).forEach(i -> {
						Math.random();
						Math.random();
					});
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		// 任务被取消后从任务队列删除
		//executor2.setRemoveOnCancelPolicy(true);
		// 1s后运行，此时任务还在队列中
		ScheduledFuture<?> future = executor2.schedule(r, 1, TimeUnit.SECONDS);
		// 任务取消，但还在队列中且不再运行
		System.out.println(future.cancel(false));
		System.out.println(executor2.getQueue().size());
	}
	
	/**
	 * cancel(true)：执行中的任务thread.interrupt()设置中断标识
	 * @throws InterruptedException
	 */
	public void test7() throws InterruptedException {
		Runnable r = () -> {
			String name = Thread.currentThread().getName();
			try {
				while(true) {
					if (Thread.currentThread().isInterrupted()) {
						throw new InterruptedException(name + "中断了");
					}
					System.out.println(name);
					IntStream.range(0, 123456).forEach(i -> {
						Math.random();
						Math.random();
					});
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		// 任务立即运行，不放入任务队列
		ScheduledFuture<?> future = executor2.schedule(r, 0, TimeUnit.SECONDS);
		Thread.sleep(1000);
		System.out.println(executor2.getQueue().size());
		// 任务取消，执行中的任务设置中断标识
		System.out.println(future.cancel(false));
	}
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		new ScheduledExecutorService_().test7();
	}
}
