package com.java.util.concurrent.线程池;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

/**
 * ExecutorService
 * 
 * 	实例方法
 * 		execute方法：提交任务，无返回值
 * 		submit方法：提交任务，有返回值
 * 		invokeAny()：返回第一个完成的任务的返回值，其余执行中的线程设置中断标识。
 * 			任务异常时默认不打印异常信息，且不会影响主线程。
 * 			如果最先完成的任务出现异常，则取下一个完成的任务返回值；如果所有任务都有异常则主线程ExecutionException
 * 		invokeAny(Collection, long, timeUnit)：如果在指定时间内没有完成的任务，则主线程TimeoutException
 * 		invokeAll(Collection)：阻塞，返回所有任务的执行结果。所有任务执行完毕再继续
 * 			任务线程执行时发生异常不打印错误信息且不影响其它线程，当future.get()时异常会被抛到主线程。如果不被catch则主线程结束
 * 		invokeAll(Collection, long, timeUnit)：如果在指定时间内没有完成所有任务，则取消任务且主线程继续执行。
 * 			被取消的任务future.get()时CancellationException，已完成的任务正常取值
 * 注意：实验时不要用junit，其结果与main()不同
 * @author Administrator
 *
 */
public class ExecutorService_ {

	private ExecutorService executor = Executors.newCachedThreadPool();
	private List<Callable<String>> list = new ArrayList<Callable<String>>();
	
	/**
	 * invokeAny()：返回第一个完成的任务的返回值，其余执行中的线程设置中断标识。
	 * 任务异常时默认不打印异常信息，且不会影响主线程。
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void test1() throws InterruptedException, ExecutionException {
		// 快的任务正确，慢的任务异常
		// A 先执行完并将B设置中断标识
		list.add(() -> {
			System.out.println(Thread.currentThread().getName() + " begin");
			IntStream.range(0, 123456).forEach(i -> Math.random());
			System.out.println(Thread.currentThread().getName() + " end");
			return "A";
		});
		// B 默认不打印异常信息，所以用try-catch
		list.add(() -> {
			System.out.println(Thread.currentThread().getName() + " begin");
			try {
				for(int i = 0; i < 223456; i++) {
					if (Thread.currentThread().isInterrupted()) {
						throw new RuntimeException("线程已中断");
					} else {
						Math.random();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " end");
			return "B";
		});
		String value = executor.invokeAny(list);
		System.out.println("==========" + value);
	}
	
	/**
	 * invokeAny()：如果最先完成的任务出现异常，则取下一个完成的任务返回值；如果所有任务都有异常则主线程ExecutionException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void test2() throws InterruptedException, ExecutionException {
		// 慢的任务正确，快的任务异常
		// A
		list.add(() -> {
			System.out.println(Thread.currentThread().getName() + " begin");
			IntStream.range(0, 123456).forEach(i -> {
					throw new RuntimeException("出现异常");
			});
			System.out.println(Thread.currentThread().getName() + " end");
			return "A";
		});
		// B
		list.add(() -> {
			System.out.println(Thread.currentThread().getName() + " begin");
			for(int i = 0; i < 223456; i++) {
				Math.random();
			}
			System.out.println(Thread.currentThread().getName() + " end");
			return "B";
		});
		String value = executor.invokeAny(list);
		System.out.println("==========" + value);
	}
	
	/**
	 * invokeAny(Collection, long, timeUnit)：当超时且任务线程异常时，如果主线程先超时则主线程TimeoutException，如果任务线程先异常则两个线程都报错（任务线程需要try-catch）
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public void test3() throws InterruptedException, ExecutionException, TimeoutException {
		// 显式抛出异常
		list.add(() -> {
			IntStream.range(0, 123456).forEach(i -> Math.random());
			try {
				throw new Exception("出现异常");
			} catch (Exception e) {
				e.printStackTrace();
				// 打印异常信息后要重新抛出，否则此线程正常结束并返回结果
				throw new Exception("出现异常");
			}
		});
		String value = executor.invokeAny(list, 1, TimeUnit.MILLISECONDS);
		System.out.println("==========" + value);
	}
	
	/**
	 * invokeAll(Collection)：阻塞，返回所有任务的执行结果。所有任务执行完毕再继续
	 * 		任务线程执行时发生异常不打印错误信息且不影响其它线程，当future.get()时异常会被抛到主线程。如果不被catch则主线程结束
	 */
	public void test4() throws InterruptedException, ExecutionException {
		// A
		list.add(() -> {
			System.out.println(Thread.currentThread().getName() + " begin");
			IntStream.range(0, 123456).forEach(i -> Math.random());
			System.out.println(Thread.currentThread().getName() + " end");
			return "A";
		});
		// B，运行时不打印异常信息
		list.add(() -> {
			System.out.println(Thread.currentThread().getName() + " begin");
			IntStream.range(0, 223456).forEach(i -> Math.random());
			throw new Exception("出现异常了");
		});
		List<Future<String>> result = executor.invokeAll(list);
		// 注意，此时所有任务已执行完毕
		System.out.println("==========");
		for (Future<String> future: result) {
			// get()时，任务线程的异常会被抛到主线程，不被catch则线程终止
			System.out.println(future.get());
		}
		// main线程已经终止，此句无法打印
		System.out.println("main end");
	}
	
	/**
	 * invokeAll(Collection, long, timeUnit)：如果在指定时间内没有完成所有任务，则取消任务且主线程继续执行。
	 * 被取消的任务future.get()时CancellationException，已完成的任务正常取值
	 */
	public void test5() throws InterruptedException, ExecutionException {
		// A，超时被取消，future.get()时CancellationException
		list.add(() -> {
			System.out.println(Thread.currentThread().getName() + " begin");
			IntStream.range(0, 123456).forEach(i -> {
				Math.random();
				Math.random();
			});
			System.out.println(Thread.currentThread().getName() + " end");
			return "A";
		});
		// B，正常完成
		list.add(() -> {
			System.out.println(Thread.currentThread().getName() + " begin");
			IntStream.range(0, 2).forEach(i -> {
				Math.random();
				Math.random();
			});
			//throw new Exception("出现异常了");
			return "B";
		});
		List<Future<String>> result = executor.invokeAll(list, 10, TimeUnit.MILLISECONDS);
		System.out.println("==========" + result.size());
		for (Future<String> future: result) {
			// get()时，超时的任务报错并抛到主线程
			System.out.println(future.get());
		}
		// main线程已经终止，此句无法打印
		System.out.println("main end");
	}
	
	public static void main(String[] args) throws Exception {
		new ExecutorService_().test5();
	}
}
