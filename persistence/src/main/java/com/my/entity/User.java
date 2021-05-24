package com.my.entity;

import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA实体类
 * @author nerv
 *
 */
@Entity(name = "user")		// 指定为基于JPA规范的实体类，name为表名；也可@Entity @Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {

	@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)	// 主键生成策略
	private Integer id;
	
	private String name;
	
	@Column(name = "sex")	// 映射数据库表字段，若与字段名相同可以不设置
    private Integer sex;
	
	public User(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
}