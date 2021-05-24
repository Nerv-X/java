package com.javax.persistence;
/**
 * 类注解
 * 	@Entity					持久化类，默认表名同类名
 * 		name					指定数据库表名
 * 	@Table
 * 		name					指定数据库表名，优先级高于@Entity(name=)
 * 		catalog					数据库目录
 * 		schema					数据库模式
 * 		uniqueConstraints		约束条件
 * 属性注解
 * 	@Id						主键列
 * 	@GeneratedValue	
 * 		strategy				主键生成策略，JPA默认AUTO
 * 								策略见GenerationType
 * 		generator				当GenerationType.TABLE时，对应@TableGenerator(name=)
 * 								当GenerationType.SEQUENCE时，对应@SequenceGenerator(name=)
 * 	@TableGenerator
 * 		name					名称，用于@GeneratedValue(generator=)
 * 		table					存储主键的表
 * 		pkColumnName			主键列名
 * 		valueColumnName			主键值，每次使用自动+allocationSize
 * 		pkColumnValue			主键对应的类属性名称
 * 		allocationSize			步长
 * 	@SequenceGenerator
 * 		name					名称，用于@GeneratedValue(generator=)
 * 		initialValue			初始值，Oracle默认0，一般设为1
 * 		allocationSize			步长，Oracle默认50，一般设为1
 * 		sequenceName			序列名
 * 	@Column
 * 		name					数据库列名
 * 		unique					唯一，默认false
 * 		nullable				可为空，默认true
 * 		length					长度，默认255
 * 		columnDefinition		在数据库中的数据类型
 * 	@Temporal				时间精确度，用于java.util.Date 或java.util.Calendar，默认TemporalType.TIMESTAMP
 * 	@Basic					映射字段，所有字段默认@Basic
 * 		fetch					加载机制，默认FetchType.EAGER
 * 		optional				可为空，默认true
 * 	@Transient				非映射字段
 * 
 * 冷门注解
 * 	@Transaction		提供了在类和方法级别声明性地控制CDI托管bean上的事务边界的能力，用于Java EE规范定义为托管bean的类，方法级注释覆盖类级
 * 						基于JavaEE的CDI规范，服务于EJB
 * 其它依赖包注解
 * 	@JsonFormat			Jackson注解，请求/响应时json中字段格式
 * 		pattern				格式，如"yyyy-MM-dd HH:mm:ss"
 * 		timezone			时区，如"GMT+8"
 * 
 * 持久化类必需：
 * 		@Entity
 * 		@Id
 * 		public 或 protected的无参构造
 * 		是顶级类，枚举或接口不可以
 * 		不能是final类，不能有final方法
 * 
 * ※※※※※※※※※※※※※※※※※※※※※※※※※※※重要※※※※※※※※※※※※※※※※※※※※※※※※※※※
 * 		属性注解也可使用在getter方法上，但每个持久化类中必须统一写在方法上或全部在
 * 		属性上，否则注解不生效且不报错
 * ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
 * 
 * @author nerv
 *
 */
import javax.persistence.*;

public class 基本注解 {

	/*
	 * ===========================枚举===========================
	 */
	/**
	 * 主键生成策略
	 * 		AUTO：默认，JAP自动选择
	 * 			SqlServer - identity
	 * 			MySql - auto increment
	 * 		IDENTITY：数据库自增，oracle 12g之前不支持
	 * 		SEQUENCE：数据库序列产生，@SequenceGenerator指定序列名，Mysql不支持
	 * 		TABLE：用表存主键，每次加1，可移植性高
	 */
	public GenerationType generationType() {
		return GenerationType.AUTO;
	}
	
	/**
	 * 时间精确度，对应数据库的三种精确度
	 * 		TIMESTAMP			默认，年月日时分秒
	 * 		DATE				精确到年月日
	 * 		TIME				精确到时分秒
	 * @return
	 */
	public TemporalType temporalType() {
		return TemporalType.TIMESTAMP;
	}
	
	/**
	 * 加载机制
	 * 		EAGER		即时加载，实例化对象时不加载该属性，默认
	 * 		LAZY		懒加载，只有第一次调用该属性时才加载
	 * @return
	 */
	public FetchType fetchType() {
		return FetchType.EAGER;
	}
	 
	/*
	 * ===========================详解及示例===========================
	 */
		
	/**
	 * @Column(columnDefinition="")
	 * 	类型，不区分大小写
	 * 		String默认varchar
	 * 	示例
	 * 		"varchar(128) not null"			// 同时指定nullable、length
	 * 		"DATE DEFAULT CURRENT_DATE"		// 同时指定默认值
	 */
	public void columnDefinition() {
		
	}
	
}
