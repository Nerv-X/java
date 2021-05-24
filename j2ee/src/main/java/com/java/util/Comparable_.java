package com.java.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.my.entity.User;

/**
 * 集合排序
 * 		自然排序 - 根据Comparable.compareTo()比较大小并升序排列
 * 
 * @author nerv
 *
 */
public class Comparable_ {

	/**
	 * List实现Comparable接口排序，欲将集合中自定义元素排序，则自定义的类必须实现Comparable接口
	 */
	@Test
	public void sortList(){
		List<User> list = User.list();
		Collections.sort(list);
		System.out.println(list);
	}
	/**
	 * TreeSet实现Comparable接口排序
	 */
	@Test
	public void sortHashTree2(){
		TreeSet<User> set = new TreeSet<>();
		set.addAll(User.list());
		System.out.println(set);//TreeSet add元素时自动排序
	}
	/**
	 * TreeSet创建比较器传入构造方法
	 */
	@Test
	public void sortHashTree(){
		Set<User> set = new TreeSet<>(new Comparator<User>(){
			@Override
			public int compare(User u1, User u2) {
				return u2.compareTo(u1);
			}
		});
		set.addAll(User.list());
		for(Iterator<User> it = set.iterator(); it.hasNext();){	//Set集合无序，没有get()
			System.out.println(it.next());
		}
			
	}

}
