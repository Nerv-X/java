package com.java.util.concurrent.fork_join分治;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * ForkJoinTask
 * 	静态方法
 * 		invoke(action1, action2)：分离任务（优化效率），递归调用fork()
 * 	实例方法
 * 		fork()：分离任务
 * 		isDone()：任务是否已完成
 * 		isCompletedAbnormally()：任务是否异常
 * 		isCompletedNormally()：任务是否正常执行完毕
 * 		getException()：返回异常
 * RecursiveAction		Fork/Join框架中执行一次无返回值的任务
 * 
 * 	案例
 * 		快速排序
 * 	
 * RecursiveTask分解任务，任务异常只有get()/join()才会被抛到主线程
 * 	实例方法
 * 		get()：阻塞，同一pool的任务全部执行完毕后再继续
 * 			检查异常，任务异常时在主线程抛出ExecutionException
 * 		join()：阻塞，同一pool的任务全部执行完毕后再继续
 * 			运行时异常，任务异常时在主线程抛出异常。
 * 	案例
 * 		两个整数间的全部整数和
 * @author Administrator
 *
 */
public class ForkJoinTask_ {

	private ForkJoinPool pool = new ForkJoinPool();
	
	/**
	 * RecursiveAction分解任务
	 * @throws InterruptedException
	 */
	@Test
	public void test1() throws InterruptedException {
		// 1.继承抽象类RecursiveAction，构造传参
		class MyRecursiveAction extends RecursiveAction {
			private int begin;
			private int end;
			public MyRecursiveAction(int begin, int end) {
				this.begin = begin;
				this.end = end;
			}
			@Override
			protected void compute() {
				System.out.println("执行fork()分解任务");
				if (end - begin > 2) {
					int middle = (begin + end) / 2;
					MyRecursiveAction leftAction = new MyRecursiveAction(begin, middle);
					MyRecursiveAction rightAction = new MyRecursiveAction(middle + 1, end);
					// invokeAll()可以优化效率，底层调用fork()
					ForkJoinTask.invokeAll(leftAction, rightAction);
				} else {
					System.out.println("组合：" + begin + "--" + end);
				}
			}
		}
		// 2.构造任务池并提交任务
		pool.submit(new MyRecursiveAction(1, 10));
		Thread.sleep(3000);
	}
	/**
	 * RecursiveTask分解任务，任务异常只有get()/join()才会被抛到主线程
	 * 	get()：阻塞，同一pool的任务全部执行完毕后再继续
	 * 		检查异常，任务异常时在主线程抛出ExecutionException
	 * 	join()：阻塞，同一pool的任务全部执行完毕后再继续
	 * 		运行时异常，任务异常时在主线程抛出异常。
	 * @throws InterruptedException 
	 */
	@Test
	public void test2() throws InterruptedException{
		// 任务1：3秒后返回
		RecursiveTask<String> t1 = new RecursiveTask<String>() {
			public String compute() {
				try {
					Thread.sleep(3000);
					System.out.println("A end " + System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return "A";
			}
		};
		// 任务2：立即返回
		RecursiveTask<String> t2 = new RecursiveTask<String>() {
			public String compute() {
				System.out.println("B end " + System.currentTimeMillis());
				return "B";
			}
		};
		// 任务3：异常
		RecursiveTask<String> t3 = new RecursiveTask<String>() {
			public String compute() {
				return String.valueOf(1/0);
			}
		};
		pool.submit(t1);
		pool.submit(t2);
		pool.submit(t3);
		try {
			// get()/join()阻塞，待pool中所有任务都完成后继续
			System.out.println(t1.get() + " " + System.currentTimeMillis());
			System.out.println(t2.get() + " " + System.currentTimeMillis());
			// 取任务3结果时，主线程抛出ExecutionException。换join()则主线程直接抛出异常
			System.out.println(t3.get() + " " + System.currentTimeMillis());
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * RecursiveAction实现快速排序
	 * @throws InterruptedException
	 */
	@Test
	public void quickSort() throws InterruptedException{
		class MyRecursiveAction extends RecursiveAction {
			private int[] arr;
			private int left, right;
			public MyRecursiveAction(int[] arr, int left, int right) {
				this.arr = arr;
				this.left = left;
				this.right = right;
			}
			@Override
			protected void compute() {
				if (left < right) {
					int middle = getMiddle(arr, left, right);
					MyRecursiveAction leftAction = new MyRecursiveAction(arr, left, middle - 1);
					MyRecursiveAction rightAction = new MyRecursiveAction(arr, middle + 1, right);
					ForkJoinTask.invokeAll(leftAction, rightAction);
				}
			}
			
		}
		int[] arr = {3, 8, 1, 6, 9, 3, 4, 2, 5};
		// 任务1：3秒后返回
		RecursiveAction task = new MyRecursiveAction(arr, 0, arr.length-1);
		pool.submit(task);
		IntStream.range(0, arr.length).forEach(i -> {
			System.out.print(arr[i] + " ");
		});
	}
	//第一次遍历，返回轴值索引，左侧比轴值小，右侧比轴值大
	private int getMiddle(int[] arr, int l, int r){
		int value = arr[l];	//选取第一个值作为轴值
		while(l < r){
			while(l < r && arr[r] >= value)
				r--;
			arr[l] = arr[r];
			while(l < r && arr[l] <= value)
				l++;
			arr[r] = arr[l];
		}
		arr[l] = value;
		return l;
	}
	
	/**
	 * RecursiveTask实现两个整数间的全部整数和
	 * 		fork()与invokeAll()比较
	 */
	@Test
	public void test3() {
		class MyRecursiveTask extends RecursiveTask<Integer> {
			private int left, right;
			public MyRecursiveTask(int left, int right) {
				this.left = left;
				this.right = right;
			}
			@Override
			protected Integer compute() {
				if (right > left) {
					int middle = (left + right) / 2;
					MyRecursiveTask leftTask = new MyRecursiveTask(left, middle);
					MyRecursiveTask rightTask = new MyRecursiveTask(middle + 1, right);
					// 效率比leftTask.fork(); rightTask.fork();更高
					ForkJoinTask.invokeAll(leftTask, rightTask);
					return leftTask.join() + rightTask.join();
				} else {
					return right;
				}
			}
		}
		MyRecursiveTask task = new MyRecursiveTask(1, 900000);
		pool.submit(task);
		do {
			System.out.println("并行数：" + pool.getParallelism()
				+ "，线程池大小：" + pool.getPoolSize()
				+ "，已提交但未执行的任务数：" + pool.getQueuedSubmissionCount()
				+ "，活动的线程数：" + pool.getActiveThreadCount()
				+ "，任务总数：" + pool.getQueuedTaskCount()
				+ "，偷窃的任务数：" + pool.getStealCount()
				+ "，正在运行的非阻塞状态的线程数：" + pool.getRunningThreadCount());
		} while (!task.isDone());
		System.out.println(task.join());
	}
		
	public static void main(String[] args) throws Exception {
		new ForkJoinTask_().test3();
	}
}
