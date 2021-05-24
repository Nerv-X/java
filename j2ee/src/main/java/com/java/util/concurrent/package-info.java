/**
 * 同步辅助类
 * 		CountDownLatch		使多个线程同时执行，一组线程等待另一组线程完成后再继续执行
 * 		CyclicBarrier		使多个线程同时执行，一组线程必须全部完成某个事情后才能继续执行
 * 		Exchanger			用于2个线程间传输数据
 * 		Phaser				多线程分组控制，强于CyclicBarrier
 * 		Semaphore			控制并发线程的数量，是synchronized的升级版
 * @author nerv
 *
 */
package com.java.util.concurrent;