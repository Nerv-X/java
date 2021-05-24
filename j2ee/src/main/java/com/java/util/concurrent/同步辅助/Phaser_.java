package com.java.util.concurrent.同步辅助;

import java.util.concurrent.Phaser;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * [同步辅助类]Phaser，用于多线程分组控制，强于CyclicBarrier
 * 	区别
 * 		CyclicBarrier：不可动态添加parties；调用一次await()只占用1个parties
 * 		Phaser：可设置多屏障，动态增减parties；设置阻塞的线程是否中断
 * 	构造方法
 * 		Phaser(parties)：每parties个线程为一组
 * 	实例方法
 * 		arriveAndAwaitAdvance()：屏障，作用类似countDownLatch.await()，parties个线程执行此方法后才能继续执行
 * 		arriveAndDeregister()：阻塞，parties个线程执行此方法后才能继续执行，且parties-1
 * 		arrive()：屏障，不阻塞，继续执行。
 * 		getPhase()：第几个屏障，phase从0开始。只有本组全部线程都通过屏障时parse才加1
 * 		onAdvance(phase, parties)：通过新屏障时自动调用，此时到达但并未跨过屏障，所以此时phase与到达之前相同
 * 		getRegisteredParties()：返回当前parties
 * 		register()：parties加1
 * 		bulkRegister(n)：parties加n
 * 		getArrivedParties()：当前parties已到达的线程数
 * 		getUnarrivedParties()：当前parties未到达的线程数
 * 		awaitAdvance(phase)：如果phase==getPhase()，则线程阻塞直到phase != getPhase()；
 * 			如果phase!=getPhase()则继续执行，且不参与parties计数；不会被中断
 * 		awaitAdvanceInterruptibly(phase)：同awaitAdvance(phase)但可被中断，中断时抛异常InterruptedException
 * 		awaitAdvanceInterruptibly(phase, long, timeUnit)：线程最多阻塞指定时间，超时则报错
 * 		forceTermination()：失效屏障，不再阻塞线程
 * 		isTerminated()：屏障是否已经失效
 * @author Nerv
 *
 */
public class Phaser_ {

	// 每3个线程一组设置屏障
	private Phaser p = new Phaser(3);
	
	private Runnable r1, r2;
	
	/**
	 * arriveAndAwaitAdvance()：阻塞，parties个线程执行此方法后才能继续执行
	 */
	@Test
	public void test1() {
		// 两套执行逻辑，在每一屏障处相差5秒
		r1 = new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				System.out.println(name + "：屏障"+p.getPhase()+"，就绪于" + System.currentTimeMillis());
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				System.out.println(name + "：屏障"+p.getPhase()+"，就绪于" + System.currentTimeMillis());
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
			}
		};
		r2 = new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				try {
					System.out.println(name + "：屏障"+p.getPhase()+"，就绪于" + System.currentTimeMillis());
					Thread.sleep(5000);
					p.arriveAndAwaitAdvance();
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
					System.out.println(name + "：屏障"+p.getPhase()+"，就绪于" + System.currentTimeMillis());
					Thread.sleep(5000);
					p.arriveAndAwaitAdvance();
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		go();
	}
	
	/**
	 * arriveAndAwaitAdvance()：阻塞，parties个线程执行此方法后才能继续执行
	 * getArrivedParties()：当前parties已到达的线程数
	 * getUnarrivedParties()：当前parties未到达的线程数
	 */
	@Test
	public void test2() {
		// 两套执行逻辑，在每一屏障处相差5秒
		r1 = () -> {
				String name = Thread.currentThread().getName();
				System.out.println(name + "：屏障1，就绪于" + System.currentTimeMillis()+ "，已到达"+p.getArrivedParties()+"个线程，还差" + p.getUnarrivedParties()+"个");
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障1，结束于" + System.currentTimeMillis());
				System.out.println(name + "：屏障2，就绪于" + System.currentTimeMillis()+ "，已到达"+p.getArrivedParties()+"个线程，还差" + p.getUnarrivedParties()+"个");
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障2，结束于" + System.currentTimeMillis());
		};
		r2 = () -> {
				String name = Thread.currentThread().getName();
				try {
					System.out.println(name + "：屏障1，就绪于" + System.currentTimeMillis()+ "，已到达"+p.getArrivedParties()+"个线程，还差" + p.getUnarrivedParties()+"个");
					Thread.sleep(5000);
					p.arriveAndAwaitAdvance();
					System.out.println(name + "：屏障1，结束于" + System.currentTimeMillis());
					System.out.println(name + "：屏障2，就绪于" + System.currentTimeMillis()+ "，已到达"+p.getArrivedParties()+"个线程，还差" + p.getUnarrivedParties()+"个");
					Thread.sleep(5000);
					p.arriveAndAwaitAdvance();
					System.out.println(name + "：屏障2，结束于" + System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		};
		go();
	}
	
	/**
	 * arriveAndDeregister()：阻塞，parties个线程执行此方法后才能继续执行，且parties-1
	 * onAdvance()：通过新屏障时执行
	 * getPhase()：第几个屏障
	 */
	@Test
	public void test3() {
		p = new Phaser(3) {
			/**
			 * onAdvance(phase, parties)：
			 * 		phase从0开始，但执行onAdvance()时并未跨过此屏障，所以此时的phase与到达屏障之前相同
			 * 		如果新屏障是arriveAndDeregister，则registeredParties是减一后的结果
			 * return 是否将Phaser对象失效掉（取消屏障），true则不再阻塞线程
			 */
			@Override
			protected boolean onAdvance(int phase, int registeredParties) {
				System.out.println("=====到达但未通过第"+ (phase + 1) + "屏障，parties = " + registeredParties);
				return false;
			}
			
		};
		// 3组线程，parties分别为3、2、2
		r1 = new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
			}
		};
		r2 = new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				try {
					Thread.sleep(5000);
					System.out.println("parties = " + p.getRegisteredParties());
					p.arriveAndDeregister();
					System.out.println("parties = " + p.getRegisteredParties());
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
					new Thread(() -> {
						p.arriveAndAwaitAdvance();
						System.out.println("新线程：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
					}).start();
					Thread.sleep(5000);
					p.arriveAndAwaitAdvance();
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		
		go();
		
	}
	
	/**
	 * arrive()：屏障，不阻塞。因为只有本组全部线程都通过屏障时parse才加1，所以本组前面的线程通过时parse不变，仅最后一个线程通过时parse+1
	 */
	@Test
	public void test4() {
		// 注意parse值的变化，与test3()对比
		r1 = () -> {
				String name = Thread.currentThread().getName();
				p.arrive();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				p.arrive();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
		};
		r2 = () -> {
				String name = Thread.currentThread().getName();
				try {
					Thread.sleep(5000);
					p.arrive();
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
					Thread.sleep(5000);
					p.arrive();
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		};
		go();
	}
	
	/**
	 * arrive()：当通过arrive()的线程再次到达屏障时则屏障重置，parse+1
	 */
	@Test
	public void test5() {
		/*
		 * 现象：线程1、3快速通过两次arrive()正常结束，线程2阻塞在第一个arriveAndAwaitAdvance
		 * 分析：假设线程1的第二次arrive()快于线程3的第一个arrive()
		 * 	1）线程1连续两次arrive()，线程3第一次arrive()满足parties；parse+1 == 1
		 * 	2）线程3第二次arrive()，线程2第一次arriveAndAwaitAdvance()，此时只到达2个线程 < parties，线程2阻塞
		 */
		r1 = () -> {
				String name = Thread.currentThread().getName();
				p.arrive();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis()
						+",parse = " + p.getPhase() + ",arrivedParties="+p.getArrivedParties());
				p.arrive();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis()
						+",parse = " + p.getPhase() + ",arrivedParties="+p.getArrivedParties());System.out.println(p.getArrivedParties());
		};
		r2 = () -> {
				String name = Thread.currentThread().getName();
				try {
					Thread.sleep(5000);
					System.out.println(p.getPhase() + "--" + p.getArrivedParties());
					p.arriveAndAwaitAdvance();
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
					Thread.sleep(5000);
					p.arriveAndAwaitAdvance();
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		};
		go();
	}
	/**
	 * awaitAdvance(phase)：如果phase==getPhase()，则线程阻塞直到phase+1（即当前组线程全部通过屏障）；
	 * 如果phase!=getPhase()则继续执行，且不参与parties计数
	 * @throws InterruptedException
	 */
	@Test
	public void test6() throws InterruptedException {
		r1 = new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
			}
		};
		r2 = new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				// 如果不为p.getPhase()，即使已到达线程不等于parties也可继续执行
				p.awaitAdvance(p.getPhase());
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				p.awaitAdvance(p.getPhase());
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
			}
		};
		go();
	}
	/**
	 * awaitAdvanceInterruptibly(phase)：同awaitAdvance(phase)但可被中断，中断时抛异常InterruptedException
	 * awaitAdvance(phase)：无法被中断，执行interrupt()无反应
	 * @throws InterruptedException
	 */
	@Test
	public void test7() throws InterruptedException {
		r1 = new Runnable() {
			@Override
			public void run() {
				try {
					// 被中断会报错；如果换成p.awaitAdvance(p.getPhase())则不会被中断，相当于没执行
					p.awaitAdvanceInterruptibly(p.getPhase());
					System.out.println("A执行到了");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		};
		Thread t1 = new Thread(r1);
		t1.start();
		Thread.sleep(2000);
		t1.interrupt();
		System.out.println(Thread.currentThread().getName() + "执行到了");
	}
	
	@Test
	public void test8() {
		// 两套执行逻辑，在每一屏障处相差5秒
		r1 = new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				System.out.println(name + "：屏障"+p.getPhase()+"，就绪于" + System.currentTimeMillis());
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				p.forceTermination();
				System.out.println(name + "：" + p.isTerminated());
				System.out.println(name + "：屏障"+p.getPhase()+"，就绪于" + System.currentTimeMillis());
				p.arriveAndAwaitAdvance();
				System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
			}
		};
		r2 = new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				try {
					System.out.println(name + "：屏障"+p.getPhase()+"，就绪于" + System.currentTimeMillis());
					Thread.sleep(5000);
					p.arriveAndAwaitAdvance();
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
					System.out.println(name + "：屏障"+p.getPhase()+"，就绪于" + System.currentTimeMillis());
					Thread.sleep(5000);
					p.arriveAndAwaitAdvance();
					System.out.println(name + "：屏障"+p.getPhase()+"，结束于" + System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		go();
	}
	/**
	 * 应用：控制多线程到达屏障后是否继续运行
	 * @throws InterruptedException
	 */
	@Test
	public void test9() throws InterruptedException {
		r1 = () -> {
			String name = Thread.currentThread().getName();
			System.out.println(name + " begin = " + System.currentTimeMillis());
			p.arriveAndAwaitAdvance();
			System.out.println(name + " end = " + System.currentTimeMillis());
		};
		// 1.将parties额外加1
		p.register();
		IntStream.range(0, 3).forEach(i -> new Thread(r1).start());
		// 2.到达屏障后并不立即继续执行
		Thread.sleep(5000);
		// 3.运行继续执行
		p.arriveAndDeregister();
	}
	
	/**
	 * 多个线程测试屏障
	 */
	private void go() {
		// 每一个屏障处，3个线程几乎同一时间结束
		try {
			Thread t1 = new Thread(r1, "线程一");
			t1.start();
			Thread t2 = new Thread(r2, "线程二");
			t2.start();
			Thread t3 = new Thread(r1, "线程三");
			t3.start();
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
