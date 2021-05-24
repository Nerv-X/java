package com.java.util;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.IntStream;
/**
 * 架构
 * 	Set：不重复，自然排序（即无序）。防止重复的原理是元素需要重写hashcode()和equals()
 *		HashSet：非并发，无序
 *			LinkedHashSet：有序
 *		CopyOnWriteArraySet：并发
 *		NavigableSet(I)：支持排序（元素实现Comparable，新值如果与已有元素的compareTo()==0则不存新值），可从表头、表尾获取数据。默认自然排序，构造传入Comparator可实现定制排序
 *			TreeSet：可获取Set内容的子集，
 *			ConcurrentSkipListSet：线程安全
 * 		
 * 类/接口
 * 	Set(I)
 * 		remove(obj)：删除指定元素，成功true，失败false
 * 	NavigableSet(I)
 * 		pollFirst()：删除并返回表头元素，空集合返回null
 * 		pollLast()：删除并返回表尾元素，空集合返回null
 * @author Administrator
 *
 */
public class Set_ {

	/**
	 * HashSet：非并发
	 * CopyOnWriteArraySet：并发
	 * @throws InterruptedException
	 */
	public void test1() throws InterruptedException {
		Set<String> set = new HashSet<>();
		// 创建2个线程分别add()5000次
		new Thread(() -> {
			IntStream.range(0, 5000).forEach(i -> set.add("A" + i));
		}).start();
		new Thread(() -> {
			IntStream.range(0, 5000).forEach(i -> set.add("B" + i));
		}).start();
		Thread.sleep(1000);
		// 数量 < 10000，原因：多个线程在同一位置赋值，预期size+2实际size+1
		System.out.println(set.size());
	}
	
	/**
	 * ConcurrentSkipListSet
	 * 		支持排序：元素必须实现Comparable并排序
	 * 		不重复：新元素.compareTo(已有的元素) == 0则新值不放入集合
	 */
	public void test3() {
		// 1.元素必须实现Comparable并排序
		class UserInfo implements Comparable<UserInfo> {
			private int id;
			private String name;
			public UserInfo(int id, String name) {
				this.id = id;
				this.name = name;
			}

			@Override
			public int compareTo(UserInfo o) {
				if (o == null) {
					return 1;
				}
				// 如果不比较name则当id相同时，新对象不放入集合
				if (this.id == o.id) {
					return this.name.compareTo(o.name);
				}
				return this.id - ((UserInfo) o).id;
			}
		}
		ConcurrentSkipListSet<UserInfo> set = new ConcurrentSkipListSet<>();
		set.add(new UserInfo(3, "user3"));
		set.add(new UserInfo(4, "user4"));
		set.add(new UserInfo(2, "user2"));
		set.add(new UserInfo(1, "user1"));
		set.add(new UserInfo(1, "user1a"));
		// 2. 从集合表头开始按顺序取出元素
		while (!set.isEmpty()) {
			UserInfo u = set.pollFirst();
			System.out.println(u.id + "," + u.name);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new Set_().test1();
	}
}
