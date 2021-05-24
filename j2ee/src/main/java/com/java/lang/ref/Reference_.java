package com.java.lang.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;

import com.java.lang.ref.*;

/**
 * Reference<T> 对象引用类型
 * 		作用：允许人工指定对象的生命周期（即被回收时机）
 * 		实现：将目标对象作为构造参数传入，创建Reference引用
 * 	构造方法
 * 		Reference(T referent) 			将目标对象作为参数传入
 * 		Reference(T referent, ReferenceQueue<? super T> q) 指定关联的目标对象和引用队列，当目标对象被回收时，Reference引用会被存入队列
 * 	实例方法
 * 		get()		返回关联的对象，若对象已被回收则返回null
 * ============================5种引用类型============================
 * 	【默认】 强引用，new创建的对象，可达性分析判断是否回收
 * 
 * 	SoftReference<T>	软引用，只有Full GC时才会回收弱引用包含的对象
 * 		可用来实现内存敏感的高速缓存
 * 
 * 	WeakReference<T>	弱引用，一旦被gc扫描到则会被回收（包括yong GC和full GC）
 * 
 * 	PhantomReference	虚引用，必需ReferenceQueue，可达性分析会忽略虚引用，因此若某对象只被虚引用持有则会被回收
 * 		​主要用于追踪对象gc回收的活动，通过查看引用队列中是否包含此虚引用来判断它是否即将被回收
 * 		get()	永远返回null
 * 
 * 	FinalReference	用于收尾机制
 * 		唯一子类Finalizer，详见Finalizer_
 * 
 * 
 * ReferenceQueue	底层是链表，存储Reference实例，通过wait()和notifyAll()与对象锁实现生产者和消费者模式，以此模拟一个队列
 * 		static ReferenceQueue<Object> NULL = new Null<>();	// 标识该引用已被当前队列移除过
 * 		static ReferenceQueue<Object> ENQUEUED = new Null<>();	// 标识该引用已加入当前队列
 * LRUCache
 * 		Cache是一种用于提高系统性能，提高数据检索效率的机制，LRU(Least recently used，最近最少使用）算法和Cache的结合是最常见的Cache实现
 * 		LRU是数据冷热治理的一种思想，不常使用的冷数据分配很少的资源或提前释放，可节省资源
 * 		LRUCache有多种实现方式，本例使用双向链表+hash表实现（LinkedHashMap自身便是双向链表+hash表）
 * 
 * 
 * 参考：https://www.cnblogs.com/cord/p/11546303.html
 * 
 * @author nerv
 *
 */
public class Reference_ {

	/**
	 * 强引用，存24个元素即OutOfMemoryError，说明remove()无法释放内存
	 * 软引用，内存空间不足会Full GC回收弱引用对象，调用finalize()打印提示，不会内存溢出
	 * 
	 * VM参数：调小内存，打印GC详情
	 * 	-Xmx32M -Xms16M -XX:+PrintGCDetails
	 */
	@Test
	public void reference1() {
		int capcity = 5;
		// SoftReference<Reference_> 换 Reference_ 测试强引用
		LinkedHashMap<Integer, SoftReference<Reference_>> cache = new LinkedHashMap<>(capcity);
		IntStream.range(0, Integer.MAX_VALUE).forEach(i -> {
			// 只缓存5个元素，多了则删除最早的
			if (cache.size() >= capcity) {
				cache.remove(cache.entrySet().iterator().next().getKey());
			}
			// 新数据放队尾
			cache.put(i, new SoftReference<>(new Reference_()));
			try {
				// 睡眠1秒以确保回收线程Full GC
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("已存入" + i);
		});
	}
	
	/**
	 * 弱引用，被GC扫描到则回收，其后软引用本身被存入引用队列中
	 * 虚引用，可达性分析会忽略虚引用，因此若某对象只被虚引用持有则会被回收
	 * @throws InterruptedException 
	 */
	@Test
	public void reference2() throws InterruptedException {
		ReferenceQueue<Reference_> rq = new ReferenceQueue<>();
        // 这里必须用new String构建字符串，而不能直接传入字面常量字符串
        Reference<Reference_> r = new WeakReference<>(new Reference_(), rq);	// 换 PhantomReference
        Reference<?> rf;
        // 一次System.gc()并不一定会回收A，所以要多试几次
        while((rf=rq.poll()) == null) {
        	System.out.println("执行gc()");
            System.gc();
            TimeUnit.SECONDS.sleep(1);
        }
        // 引用指向的对象已经被回收，返回null
        System.out.println(rf.get());
        // 对象被回收后，引用本身被存入引用队列中
        System.out.println(r == rf);
	}
	
	@SuppressWarnings("unused")
	private final byte[] data = new byte[2 << 19];	// 1M
	
	/**
	 * 在GC的标记阶段被调用
	 */
	@Override
	protected void finalize() {
		System.out.println("GC标记阶段 对象即将被回收");
	}
}
