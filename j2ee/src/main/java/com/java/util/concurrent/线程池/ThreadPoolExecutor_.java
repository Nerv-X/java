package com.java.util.concurrent.线程池;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * 构造：
 * 	ThreadPoolExecutor(int corePoolSize,	核心线程数
                       int maximumPoolSize,	最大线程数
                       long keepAliveTime,	当线程数 > corePoolSize时，超过此时间则删除；为0时完成任务立即删除
                       TimeUnit unit,
                       BlockingQueue<Runnable> workQueue)	任务队列，仅保存execute()提交的Runnbale
                       )
         
   情景
   	假设X = 任务数 - corePoolSize
   	1）任务数 <= corePoolSize
   			立即创建新线程执行任务，不放入任务队列
   	2）任务数 > corePoolSize && 任务数 <= maximumPoolSize && LinkedBlockingDeque
   			将X个任务放入任务队列等待运行，忽略keepAliveTime（即不删除线程）
   	3）任务数 > corePoolSize && 任务数 <= maximumPoolSize && SynchronousQueue
   			立即创建新线程执行任务，不放入任务队列。任务执行完keepAliveTime后删除X个线程
   	4）任务数 > maximumPoolSize && LinkedBlockingDeque
   			将X个任务放入任务队列等待运行，忽略keepAliveTime
   	5）任务数 > maximumPoolSize && SynchronousQueue
   			处理maximumPoolSize个任务，剩余任务不再处理并抛异常
   	6）任务数 > maximumPoolSize && ArrayBlockingQueue
   			
 * 任务队列
 * 		LinkedBlockingDeque：可定义最大容量
 * 		ArrayBlockingQueue：必须定义最大容量
 * 		SynchronousQueue：无法定义最大容量，默认0
 * 线程池从任务队列按顺序取任务，但执行顺序是乱序的
 * 
 * 拒绝策略
 * 		AbortPolicy：默认，任务被拒绝会抛异常
 * 		CallerRunsPolicy：以调用线程池的线程处理被拒绝的任务
 * 		DiscardOldestPolicy：丢弃任务队列最早的任务，再将被拒绝的任务放入队列
 * 		DiscardPolicy：丢弃被拒绝的任务
 * 实例方法
 * 		shutdown()：未执行完的线程继续执行，不再执行新任务
 * 		shutdownNow()：未执行完的线程执行interrupt()，不再执行新任务。并返回任务队列中剩余任务
 * 		isShutdown()：判断线程池是否已销毁
 * 		isTerminationg()：shutdown()/shutdownNow()后但未完全关闭时返回true
 * 		awaitTermination(long, timeUnit)：阻塞，最多等待指定时间直到线程池关闭，返回是否已关闭。用于shutdown()后
 * 		setThreadFactory()：自定义线程工厂
 * 		get/setRejectedExecutionHandler()：任务被拒绝时的行为
 * 		allowsCoreThreadTimeOut()：配置的核心线程是否有超时效果
 * 		prestartCoreThread()：如果当前线程数<coreSize则创建一个线程并返回true，否则false
 * 		prestartCoreThreads()：创建剩余核心线程，并返回创建的数量
 * 		beforeExecute()/afterExecute()：执行execute()的前后逻辑。需要继承ThreadPoolExecutor并重写方法
 * 		remove(runnable)：删除任务队列中未执行的任务，无法删除submit()提交的任务；删除失败不报错
 * 		getCompletedTaskCount()：已完成的任务数
 * 		getActiveCount()：正在执行任务的线程数
 * 		getCompletedTaskCount()：线程池中执行完任务的线程数
 * 		getPoolSize()：线程池中全部线程数
 * 		getCorePoolSize()：线程池中核心线程数
 * 		getMaximumPoolSize()：线程池的最大线程数
 * 		getTaskCount()：发送到线程池的任务数
 * 		execute(runnable)：执行任务，无返回值
 * 		submit(runnable)：执行无返回值的任务，返回Future
 * 		submit(callable)：执行有返回值的任务，返回Future
 * @author Administrator
 *
 */
public class ThreadPoolExecutor_ {
	// 定义任务内容
	private Runnable r = () -> {
		try {
			System.out.println(Thread.currentThread().getName());
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	};
	
	private Runnable r2 = () -> {
		try {
			System.out.println(Thread.currentThread().getName() + " " + System.currentTimeMillis());
			Thread.sleep(4000);
			System.out.println(Thread.currentThread().getName() + " " + System.currentTimeMillis());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	};
	// 定义任务队列
	BlockingQueue<Runnable> workQueue;
	
	/**
	 * 情景1，情景2，情景4：LinkedBlockingDeque时最多有corePoolSize个线程，所以忽略keepAliveTime
	 * @throws InterruptedException
	 */
	@Test
	public void test1() throws InterruptedException {
		workQueue = new ArrayBlockingQueue<>(2);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(7, 8, 0, TimeUnit.SECONDS, workQueue);
		// 7个任务验证情景一，8个验证情景二，12个验证情景四
		IntStream.range(0, 7).forEach(i -> executor.execute(r));
		test(executor);
	}
	
	/**
	 * 情景3：SynchronousQueue时任务不放进任务队列，keepTimeAlive有效
	 */
	@Test
	public void test2() {
		workQueue = new SynchronousQueue<>();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(7, 10, 0, TimeUnit.SECONDS, workQueue);
		// 10个任务验证情景三，13个验证情景五
		IntStream.range(0, 13).forEach(i -> executor.execute(r));
		test(executor);
	}
	
	/**
	 * BlockingQueue 任务队列，超过容量则拒绝任务并报错
	 * 	LinkedBlockingDeque，可以无参或有参
	 * 		new LinkedBlockingDeque() = new LinkedBlockingDeque(Integer.MAX_VALUE)
	 * 	ArrayBlockingQueue，必须有参
	 * 	SynchronousQueue，无参，容量0
	 * @throws InterruptedException
	 */
	@Test
	public void test9() throws InterruptedException {
		// 带参构造：最大容量2
		workQueue = new ArrayBlockingQueue<>(2);
		// 7个任务：执行3个，放入队列2个，剩余2个被拒绝并报错
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 3, 0, TimeUnit.SECONDS, workQueue);
		IntStream.range(0, 7).forEach(i -> executor.execute(r));
		test(executor);
	}
	
	/**
	 * RejectedExecutionHandler：拒绝策略，默认AbortPolicy
	 * @throws InterruptedException
	 */
	@Test
	public void test10() throws InterruptedException {
		// 带参构造：最大容量2
		workQueue = new SynchronousQueue<>();
		// 7个任务：执行3个，放入队列2个，剩余2个被拒绝
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 3, 0, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
		IntStream.range(0, 7).forEach(i -> executor.execute(r));
		test(executor);
	}
	
	private void test(ThreadPoolExecutor executor) {
		try {
			Thread.sleep(300);	// 等全部线程启动
			System.out.println("========任务执行中========");
			System.out.println("maxPoolSize = " + executor.getMaximumPoolSize());
			System.out.println("corePoolSize = " + executor.getCorePoolSize());
			System.out.println("poolSize = " + executor.getPoolSize());
			System.out.println("queueSize = " + executor.getQueue().size());
			Thread.sleep(3000);
			System.out.println("========任务执行后========");
			System.out.println("maxPoolSize = " + executor.getMaximumPoolSize());
			System.out.println("corePoolSize = " + executor.getCorePoolSize());
			System.out.println("poolSize = " + executor.getPoolSize());
			System.out.println("queueSize = " + executor.getQueue().size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * shutdown()：未执行完的线程继续执行，不再执行新任务
	 * @throws InterruptedException
	 */
	@Test
	public void test3() throws InterruptedException {
		r = () -> {
			IntStream.range(0, Integer.MAX_VALUE / 10000).forEach(i -> {
				new String();
				Math.random();
			});
			System.out.println(Thread.currentThread().getName() + "执行完毕");
		};
		ThreadPoolExecutor executor = new ThreadPoolExecutor(7, 8, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
		IntStream.range(0, 7).forEach(i -> executor.execute(r));
		Thread.sleep(200);
		executor.shutdown();
	}
	/**
	 * shutdownNow()：未执行完的线程执行interrupt()，不再执行新任务
	 * 	注意：测试时不要线程在sleep时中断，否则直接中断线程
	 * @throws InterruptedException
	 */
	@Test
	public void test4() throws InterruptedException {
		r = () -> {
			IntStream.range(0, Integer.MAX_VALUE / 10000).forEach(i -> {
				new String();
				Math.random();
			});
			String name = Thread.currentThread().getName();
			// interrupt()设置了中断标识，如果要中断线程还要throw new InterruptedException()
			if(Thread.currentThread().isInterrupted()) {
				System.out.println(name + "已中断");
			} else {
				System.out.println(name + "执行完毕");
			}
		};
		ThreadPoolExecutor executor = new ThreadPoolExecutor(7, 8, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
		IntStream.range(0, 7).forEach(i -> executor.execute(r));
		Thread.sleep(200);
		executor.shutdownNow();
	}
	
	/**
	 * awaitTermination(long, timeUnit)：阻塞，最多等待指定时间直到线程池关闭，返回是否已关闭。用于shutdown()后
	 * @throws InterruptedException
	 */
	@Test
	public void test6() throws InterruptedException {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 99999, 999L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
		executor.execute(r2);
		// 不关闭则会等待10秒，返回false，证明阻塞性
		executor.shutdown();
		System.out.println("main begin " + System.currentTimeMillis());
		System.out.println("线程池是否已关闭：" + executor.awaitTermination(10, TimeUnit.SECONDS));
		System.out.println("main end " + System.currentTimeMillis());
	}
	
	/**
	 * setThreadFactory()：自定义线程工厂
	 * @throws InterruptedException
	 */
	@Test
	public void test7() throws InterruptedException {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 99999, 999L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
		// ThreadFactory + UncaughtExceptionHandler处理线程异常
		executor.setThreadFactory(r -> {
			Thread t = new Thread(r, "Thread" + new Random().nextInt(10));
			t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					System.out.println("自定义处理异常启用：" + t.getName() + " " + e.getMessage());
					e.printStackTrace();
				}
			});
			return t;
		});
		executor.execute(() -> {
			if(1/0 == 0);
		});
	}
	
	/**
	 * get/setRejectedExecutionHandler()：任务被拒绝时的行为
	 */
	@Test
	public void test8() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 999L, TimeUnit.SECONDS, new SynchronousQueue<>());
		// 可以自定义类扩展Runnable，并封装任务名称，被拒绝时打印r.getName()
		executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				System.out.println("任务" + r.hashCode() + "被拒绝");
			}
		});
		IntStream.range(0, 3).forEach(i -> executor.execute(r));
	}
	
	/**
	 * beforeExecute()/afterExecute()：执行execute()的前后逻辑
	 */
	@Test
	public void test11() {
		class MyPoolExecutor extends ThreadPoolExecutor {

			public MyPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
					BlockingQueue<Runnable> workQueue) {
				super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
			}
			
			@Override
			public void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
				System.out.println(Thread.currentThread().getName() + "执行完毕");
			}
			
			@Override
			public void beforeExecute(Thread t, Runnable r) {
				super.beforeExecute(t, r);
				System.out.println(t.getName() + "准备执行");
			}
			
		}
		workQueue = new ArrayBlockingQueue<>(2);
		ThreadPoolExecutor executor = new MyPoolExecutor(7, 8, 0, TimeUnit.SECONDS, workQueue);
		IntStream.range(0, 7).forEach(i -> executor.execute(() -> {}));
	}
}
