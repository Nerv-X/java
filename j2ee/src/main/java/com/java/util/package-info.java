/**
 * 集合架构
 * 		Iterable：迭代循环
 *			Collection：提供集合框架最主要的操作（增删改查）
 *				List：允许根据索引位置操作数据。可重复，有序
 *					ArrayList：非线程安全
 *					Vector：线程安全。不支持Iterator并发的删除（即多个线程调用vector.iterator()后，再调用vector.remove()报错ConcurrentModificationException）
 *						Stack：可实现后进先出的对象堆栈
 *				Queue：可方便地操作列头
 *					PriorityQueue：非并发（线程不安全），基于优先级的无界优先级队列
 *					Deque(I)：额外支持对表尾操作（双端队列）
 *						ArrayDeque：非并发
 *						LinkedList：除了支持从两端获取数据，还可根据索引位置操作数据
 * @author nerv
 *
 */
package com.java.util;