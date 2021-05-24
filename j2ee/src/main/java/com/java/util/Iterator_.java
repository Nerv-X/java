package com.java.util;

import java.util.*;

import org.junit.Test;
/**
 * Iterator 迭代器
 * 		next()	通过iterator()获取迭代器后，其内容如果发生更改，则不可再调用iterator.next()，否则ConcurrentModificationException
 * @author nerv
 *
 */
public class Iterator_ {

	/**
	 * next()
	 */
	@Test
	public void test1() {
		Set<Integer> set = new HashSet<>();
		set.add(1);
		Iterator<Integer> iterator = set.iterator();
		set.add(2);
		// 集合内容改变，不可再next()
		System.out.println(iterator.next());
	}
	
	
}
