package com.java.lang.reflect;

import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 * GenericDeclaration
 * 		意义：可以声明类型变量的所有实体的公共接口；即定义了哪些地方可以声明泛型变量
 * 		实现类：Class、Method、Constructor	只能在类中这3个地方定义泛型
 * 
 * 	方法
 * 		TypeVariable<?>[] getTypeParameters()		返回声明顺序的TypeVariable对象的数组
 * @author nerv
 *
 */
public class GenericDeclaration_ {

	public static void main(String[] args) {
		TypeVariable<Class<GenericDeclarationTest>>[] arr = GenericDeclarationTest.class.getTypeParameters();
        for(TypeVariable<Class<GenericDeclarationTest>> v : arr) {
            System.out.println(v.getName());
        }
	}
}

// 1.类上定义泛型
class GenericDeclarationTest<T, M> {
	// 2.构造方法上定义泛型
	public <J> GenericDeclarationTest(J j) {
		
	}
	// 3.普通方法上定义泛型
	public <K> void method(K k) {

	}
	// 属性只能使用泛型，不能定义，因为Field没有实现GenericDeclaration接口
	public List<T> field;
}