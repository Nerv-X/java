package com.java.lang;

import java.lang.Runtime;

public class System_ {

	/**
	 * 显式触发GC：
	 * 	触发前提
	 * 		禁用JVM参数-XX:+DisableExplicitGC，详见《JVM启动参数》
	 * 	执行过程
	 * 		暂停整个JVM进程，若为并行回收器且-XX:+ExplicitGCInvokesConcurrent则并行Full GC；否则常规Full GC
	 * 	常见场景
	 * 		1.分配了RMI/NIO下的堆外内存，需要Full GC才能回收
	 * 		2.使用了WeakReference和SoftReference
	 * 			WeakReference - 只要发生GC，无论Young GC或Full GC 都会被回收
	 * 			SoftReference - 只有Full GC时才会被回收。可通过System.gc()显式触发
	 * 	
	 */
	public void gc() {
		// 底层执行native方法
		Runtime.getRuntime().gc();
		/*
		 * Hotspot源码在Runtime.c的函数Java_java_lang_Runtime_gc
		 * 	回收器的处理过程：
		 * 		G1 GC			- 	g1CollectedHeap.cpp
		 * 		ZGC 			-	zDriver.cpp
		 * 		Shenandoah GC 	-	shenandoahControlThread.cpp
		 */
	}
}
