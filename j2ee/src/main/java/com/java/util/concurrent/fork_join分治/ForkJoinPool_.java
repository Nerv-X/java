package com.java.util.concurrent.fork_join分治;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * Fork/Join是jdk1.7提供的并行任务（分治编程）框架，将大任务分割为若干小任务，再对小任务
 * 的执行结果汇总。其使用了“工作窃取(work-stealing)”算法，即某个线程执行完毕自己的任务
 * 队列后，从其它任务队列窃取任务继续执行
 * 
 * ForkJoinPool		提供任务队列，继承了AbstractExecutorService
 * 	新/重写方法
 * 		execute(ForkJoinTask)：提交任务，无返回值。但可通过传入的RecursiveTask获得任务结果
 * 		execute(Runnable)：提交任务，无返回值，无法返回任务结果。ForkJoinTask.get()会阻塞
 * 		submit(ForkJoinTask)：提交任务，将参数ForkJoinTask返回
 * 		submit(Runnable)：有返回值，但无法返回任务结果，ForkJoinTask.get()返回null
 * 		submit(Callable)：返回ForkJoinTask
 * 		invoke(ForkJoinTask)：阻塞，返回任务结果
 * 	状态监视方法
 * 		getParallelism()：并行数，与CPU核数有关，默认等于CPU核数
 * 		getPoolSize()：获取线程池的大小
 * 	父类方法
 * 		invokeAll(Collection<Callable>)：阻塞，批量执行任务
 * ForkJoinTask		抽象，执行任务，3个子类
 * 	RecursiveAction：执行一次无返回值的任务
 * 	RecursiveTask：执行一次有返回值的任务
 * 	CountedComputer：
 * 
 * @author Administrator
 *
 */
public class ForkJoinPool_ {

	private ForkJoinPool pool = new ForkJoinPool();
	
	/**
	 * execute()：提交任务，无返回值
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void test1() throws InterruptedException, ExecutionException {
		RecursiveTask<String> task = new RecursiveTask<String>() {
			@Override
			protected String compute() {
				return Thread.currentThread().getName();
			}
		};
		pool.execute(task);
		System.out.println(task.get());
	}
	
	/**
	 * submit()：提交任务，返回值即参数
	 */
	@Test
	public void test2() {
		RecursiveTask<String> task = new RecursiveTask<String>() {
			@Override
			protected String compute() {
				return Thread.currentThread().getName();
			}
		};
		ForkJoinTask<String> returnTask = pool.submit(task);
		// 参数与返回的是同一对象
		System.out.println(task.hashCode());
		System.out.println(returnTask.hashCode());
	}
	/**
	 * isShutdown()：pool是否已销毁，shutdown()后即使有任务正在运行也返回true
	 * shutdown()：销毁pool，正在执行的任务等其执行完毕。可在shutdown()后执行task.get()
	 * shutdownNow()：销毁pool，正在执行的任务执行Thread.interrupt()设置中断标识。shutdownNow()后不可task.get()否则CancellationException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 */
	@Test
	public void test3() throws InterruptedException, ExecutionException {
		if (pool.isShutdown()) {
			return;
		}
		ForkJoinTask<?> task = pool.submit(() -> {
			try {
				System.out.println("begin" + System.currentTimeMillis());
				Thread.sleep(4000);
				System.out.println("end " + System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		Thread.sleep(300);	// 等待任务启动
		pool.shutdown();
		// shutdownNow()后不允许get()，否则CancellationException
		System.out.println(task.get());
		Thread.sleep(10000);
	}
	/**
	 * 任务池状态监控
	 */
	@Test
	public void test4() {
		ForkJoinTask<?> task = pool.submit(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		IntStream.range(0, 12).forEach(i -> pool.submit(task));
		System.out.println("CPU核数：" + Runtime.getRuntime().availableProcessors());
		System.out.println("并行数：" + pool.getParallelism());
		System.out.println("线程池大小：" + pool.getPoolSize());
		System.out.println("已提交但未执行的任务数：" + pool.getQueuedSubmissionCount());
		System.out.println("活动的线程数：" + pool.getActiveThreadCount());
		System.out.println("任务总数？？：" + pool.getQueuedTaskCount());
		System.out.println("偷窃的任务数：" + pool.getStealCount());
		System.out.println("正在运行的非阻塞状态的线程数：" + pool.getRunningThreadCount());
		System.out.println("线程池是否为静止未执行任务状态：" + pool.isQuiescent());

		do {
		} while (!task.isDone());
		System.out.println("main end!");
	}
	
}
