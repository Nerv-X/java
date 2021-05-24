package com.javax.persistence;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.my.entity.User;

/**
 * Persistence							JAP引导类，用于创建EntityManagerFactory实例
 * 	静态方法
 * 		createEntityManagerFactory(String, Map)		以SPI机制加载PersistenceProvider，再以provider根据persistence.xml中指定的persistence-unit创建工厂，
 * 													Map相当于<properties>优先级高于配置文件
 * 		createEntityManagerFactory(String)			<=> createEntityManagerFactory(String, null)
 * ------------------------------------------------------------------
 * PersistenceProviderResolverHolder	持有PersistenceProviderResolver对象，以内部类实现PersistenceProviderResolver
 * ------------------------------------------------------------------
 * PersistenceProviderResolver
 * 		getPersistenceProviders()			以SPI机制加载PersistenceProvider，加载路径为配置文件<persistence-unit><priver>
 * 		clearCachedProviders()				情况缓存的PersistenceProvider
 * ------------------------------------------------------------------
 * EntityManagerFactory					实体管理器工厂
 * 	实例方法
 * 		createEntityManager()				用于创建实体管理器实例
 * 		createEntityManager(Map)			用于创建实体管理器实例，并设置EntityManager属性
 * 		isOpen()							打开状态。实例创建后，调用close()方法前，处于打开状态
 * 		close()								关闭资源，isOpen()将返回false，其它方法无法被调用
 * ------------------------------------------------------------------
 * EntityManager						实体管理器
 * 		find(class, primary)				根据主键查找并返回实体对象
 * 		getReference(class, primary)		懒加载方式查找并返回代理对象
 * 		persist(entity)						insert，有重复主键则报错
 * 		remove(entity)						先根据主键select，有查到结果再delete
 * 		merge(entity)						新增或修改
 * 		flush()								同步持久化上下文环境的未保存信息到数据库
 * 		setFlushMode(flushModeType)			设置flush模式
 * 		getFlushMode()
 * 		refresh(entity)						用数据库数据更新实体对象的属性值
 * 		clear()								清除持久上下文环境，断开所有关联的实体。未提交的更新会被撤销
 * 		contains(entity)					判断一个实体是否属于当前持久上下文环境管理的实体
 * 		isOpen()							当前的实体管理器是否是打开状态
 * 		getTransaction()					返回资源层的事务对象EntityTransaction
 * 		close()								关闭实体管理器，除了getTransaction()和isOpen()的方法调用均报错
 * 											关闭后，活动的事务仍处于被管理状态，直到事务完成
 * 		createQuery(jpql)					使用jpql语句创建查询对象
 * 		createNamedQuery(name)				根据命名的查询语句块创建查询对象。参数为命名的查询语句
 * 		createNativeQuery(sql)				使用标准sql语句创建查询对象
 * 		createNativeQuery(sql, mapping)		使用标准sql语句创建查询对象，并指定返回结果集Map的名称
 * ------------------------------------------------------------------
 * EntityTransaction					管理资源层实体管理器的事务操作
 * 		begin()								启动事务，状态active，已经开启则IllegalStateException
 * 		commit()							提交事务
 * 		rollback()							回滚事务
 * 		setRollbackOnly()					标识事务只能被撤销。
 * 		getRollbackOnly()
 * 		isActive()							查看当前事务是否是活动的
 * 				true则不能调用begin()，否则IllegalStateException
 * 				false则不能调用commit、rollback、setRollbackOnly及getRollbackOnly方法，否则IllegalStateException
 * 
 * 
 * 		
 * 
 * @author nerv
 *
 */
public class Persistence_ {
	
	private static final EntityManagerFactory factory;
	private static final EntityManager manager;
	/*
	 * Persistence.createEntityManagerFactory(String, Map)
	 * 		以SPI机制加载hibernate-entitymanager包的HibernatePersistenceProvider
	 * 		再以provider根据persistence.xml中指定的persistence-unit创建工厂，
	 */
	static {
		// 指定persistence.xml的 persistence-unit
        String persistenceUnitName = "jpa-1";
        
        // 1.创建实体管理工厂，同一应用应该保证只有一个实例工厂
        factory = Persistence.createEntityManagerFactory(persistenceUnitName, getProperties());
        // 2.创建实体管理类
        manager = factory.createEntityManager();
	}
	
	/**
     * 相当于<properties>，优先级高于配置文件
     * @return
     */
    private static Map<String, Object> getProperties() {
     	Map<String, Object> map = new HashMap<>();
     	// 打印格式化sql
        map.put("hibernate.show_sql", true);
        map.put("hibernate.format_sql", true);
        // 逻辑与物理命名策略
        map.put("hibernate.implicit_naming_strategy", "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");
        map.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        return map;
    }
    
    /**
     * 事务：插入
     */
    public void insert() {
    	//3、创建事务管理器
        EntityTransaction transaction = manager.getTransaction();
        //4、开启事务
        transaction.begin();
        //5、进行持久化操作
        User user = new User();
        user.setId(5);
        user.setSex(0);
        user.setName("夏姬");
        manager.remove(user);
        manager.persist(user);
        //6、提交事务
        transaction.commit();
    }
    
    
    /**
     * 关闭资源
     */
    public void close() {
    	manager.close();
    	factory.close();
    }
    
	/**
	 * FlushModeType	刷新策略
	 * 		COMMIT			仅当提交事务时才能进行刷新
	 * 		AUTO			查询时刷新
	 * @return
	 */
	public FlushModeType flushModeType() {
		return FlushModeType.AUTO;
	}
	
	/*
	 * =====================================JPQL=====================================
	 * 定义
	 * 		JPQL：JPA的基于对象的数据库操作语句
	 * 格式
	 * 		select 查询属性 from 全限定类名 where 参数属性 = :参数
	 * 要点
	 * 		除了 Java 类和属性名称外，查询都是大小写不敏感的
	 * 参数
	 * 		格式1	
	 * 			声明 		:参数名
	 * 			设值			setParameter("参数名", 值);
	 * 		格式2
	 * 			声明			?参数序号
	 * 			设值			setParameter(参数序号, 值);		// 序号可以是int或String
	 */
	
	/**
     * JPQL查询：createQuery(jpql)
     * @return
     */
    public Query query() {
    	String jpql = "select p from nerv.test.Customer p where p.id=:Id";
        Query query = manager.createQuery(jpql);
    	query.setParameter("Id", new Integer(1));
    	return query;
    }
    
    /**
     * JPQL查询：createNamedQuery(name, class)
     * 		持久化类：@NamedQuery(name = "getByDate", query = "from nerv.test.Customer where createDate > ?1 order by createDate asc")
     * @return
     */
    public Query namedQuery() {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(2018, 10, 1);
    	Query query = manager.createNamedQuery("getByDate", User.class);
    	query.setParameter(1, calendar, TemporalType.DATE);
		return query;
	}
}
