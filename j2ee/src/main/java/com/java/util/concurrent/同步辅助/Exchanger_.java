package com.java.util.concurrent.同步辅助;
/**
 * Exchanger用于2个线程间传输数据，比生产者/消费者模式的wait/notify更方便
 * 
 * 	方法
 * 	exchange(text)：阻塞，直到其它线程来取数据。向其他某一线程发送text，同时返回接收到的数据
 * 	exchange(text, long timeout, TimeUnit unit)：指定时间没有其他线程来取数据则抛出超时异常
 * @author Nerv
 *
 */

import java.util.concurrent.Exchanger;


public class Exchanger_ extends Thread {

	private Exchanger<String> exchanger;
	private String threadName;
	
	public Exchanger_(Exchanger<String> exchanger, String threadName) {
		super(threadName);
		this.threadName = threadName;
		this.exchanger = exchanger;
	}
	
	@Override
	public void run() {
		try {
			// 向其它线程发送content，并返回该线程传递的数据
			System.out.println(threadName + "收到数据：" + exchanger.exchange("我是" + threadName));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 情况1：只有a运行，因为发送的数据不被其他线程接受，所以a阻塞
	 * 情况2：a，b运行，互相接收对方数据，正常结束
	 * 情况3：a，b，c运行，其中2个线程互相传输数据，第3个线程阻塞
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Exchanger<String> t1 = new Exchanger<>();
		Exchanger_ a = new Exchanger_(t1, "ThreadA");
		Exchanger_ b = new Exchanger_(t1, "ThreadB");
		//Exchanger_ c = new Exchanger_(t1, "ThreadC");
		a.start();
		b.start();
		//c.start();
		System.out.println("main end!");
	}
}
