package com.my.entity;

import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Comparable<Object> {
	
	public static List<User> list() {
		List<User> list = new ArrayList<User>();
		list.add(new User(1, "用户1", 1, BigDecimal.valueOf(93.2)));
		list.add(new User(2, "用户2", 2, BigDecimal.valueOf(63.5)));
		list.add(new User(3, "用户3", 1, BigDecimal.valueOf(96)));
		list.add(new User(4, "用户4", 2, BigDecimal.valueOf(55.9)));
		return list;
	}

	private Integer id;
	
	private String name;

    private Integer sex;
    
    private BigDecimal score;
	
	public User(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * List排序：元素必需实现Comparable接口
	 */
	@Override
	public int compareTo(Object o) {
		return this.getScore().compareTo(((User)o).getScore());
	}
	
	@Override
	public String toString() {
		return id + "-" + name + "-" + (sex==1 ? "男" : "女") + "-分数" + score;
	}
}