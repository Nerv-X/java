package com.javax.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.my.entity.User;

/**
 * Hibernate JPA 操作数据库
 * @author nerv
 *
 */
public class 案例01_Hibernate实现最简单的JPA {

	public static void main(String[] args) {
        // 指定persistence.xml的 persistence-unit
        String persistenceUnitName = "jpa-1";
        //1、创建实体管理工厂，同一应用应该保证只有一个实例工厂
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        //2、创建实体管理类
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        //3、创建事务管理器
        EntityTransaction transaction = entityManager.getTransaction();
        //4、开启事务
        transaction.begin();
        //5、进行持久化操作
        User user = new User();
        user.setId(5);
        user.setSex(0);
        user.setName("夏姬");
        entityManager.persist(user);
        //6、提交事务
        transaction.commit();
        //7、关闭资源
        entityManager.close();
        entityManagerFactory.close();
    }
}