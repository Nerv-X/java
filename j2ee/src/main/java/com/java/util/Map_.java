package com.java.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.IntStream;

import org.junit.Test;
/**
 * 架构
 * 	Map(I) 	无序
 * 		HashMap：非并发
 * 			LinkedHashMap：	非并发，有序（按put顺序）
 * 		NavigableMap(I)：支持排序，可从表头、表尾获取数据
 * 			TreeMap：	按照键的自然顺序进行排序
 * 			ConcurrentSkipListMap：并发，有序，不重复（如果新key.compareTo(旧key) == 0则不存新值）
 * 		ConcurrentHashMap：并发，无序
 * 	Hashtable：并发，但一个线程用Iterator迭代时，另一线程不可修改其数据
 * 
 * 类/接口
 * 	NavigableMap(I)
 * 		pollFirstEntry：从表头删除并返回元素
 * 		pollLastEntry：从表尾删除并返回元素
 * @author Administrator
 *
 */
public class Map_ {

	/**
	 * HashMap线程非安全：结果<10000	原因：线程A、B同时对同一位置赋值，预期size+2，实际size+1
	 * HashTable线程安全：结果=10000
	 */
	@Test
	public void test1() {
		// Hashtable<String, String> map = new Hashtable<>();
		Map<String, String> map = new HashMap<>();
		// 2线程分别放入5000数据
		Thread a = new Thread(() -> {
			IntStream.range(0, 50000).forEach(i -> {
				String s = "ThreadA" + (i + 1);
				map.put(s, s);
			});
			System.out.println(map.size());
		});
		Thread b = new Thread(() -> {
			IntStream.range(0, 50000).forEach(i -> {
				String s = "ThreadB" + (i + 1);
				map.put(s, s);
			});
			System.out.println(map.size());
		});
		a.start();
		b.start();
	}
	
	/**
	 * Hashtable用Iterator迭代时，另一线程不可修改map内容否则报错
	 * ConcurrentHashMap：无此问题
	 * 注意：Iterator的问题换成非并发的集合也是如此
	 * @throws InterruptedException
	 */
	@Test
	public void test2() throws InterruptedException {
		//ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		Hashtable<String, String> map = new Hashtable<>();
		// 1.准备数据
		IntStream.range(0, 50000).forEach(i -> {
			String s = "ThreadA" + (i + 1);
			map.put(s, s);
		});
		// 2.Iterator迭代
		Thread a = new Thread(() -> {
			Iterator<String> keyIterator = map.keySet().iterator();
			while(keyIterator.hasNext()) {
				System.out.println(keyIterator.next());
			}
		});
		// 3.另一线程增删数据，报错
		Thread b = new Thread(() -> {
			map.put("a", "a");
		});
		a.start();
		Thread.sleep(300);
		b.start();
	}
	
	/**
	 * ConcurrentSkipListMap
	 * 		支持按key排序：key必须实现Comparable并排序
	 * 		不重复：新元素key.compareTo(已有的元素的key) == 0则新值不放入集合
	 */
	@Test
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
		ConcurrentSkipListMap<UserInfo, String> map = new ConcurrentSkipListMap<>();
		map.put(new UserInfo(3, "user3"), "value");
		map.put(new UserInfo(4, "user4"), "value");
		map.put(new UserInfo(2, "user2"), "value");
		map.put(new UserInfo(1, "user1"), "value");
		map.put(new UserInfo(1, "user1a"), "value");
		while (!map.isEmpty()) {
			Map.Entry<UserInfo, String> entry = map.pollFirstEntry();
			UserInfo u = entry.getKey();
			System.out.println(u.id + "," + u.name + "," + entry.getValue());
		}
	}
	
	/**
	 * TreeMap按照键的自然顺序进行排序
	 */
	@Test
	public void treeMap() {
		TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		map.put("a", 1);
		map.put("e", 2);
		map.put("b", 3);
		map.put("f", 4);
		map.put("c", 5);
		map.put("d", 6);
		System.out.println(map);
	}
}
