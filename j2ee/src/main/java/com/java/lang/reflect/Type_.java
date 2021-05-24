package com.java.lang.reflect;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.*;

import org.junit.Test;

import com.java.lang.reflect.*;
/**
 * Type接口
 * 		实现类：Class
 * 		子接口：GenericArrayType, ParameterizedType, TypeVariable<D>, WildcardType
 * 		作用： 统一传统的Class与泛型衍生的4种新类型，使得泛型支持反射
 * 
 * 子接口，对应泛型<>内的4种由泛型衍生出的特殊情况
 * 	- ParameterizedType		参数化类型，即包含<>的类，如Collection<E>
 * 	- WildcardType			通配符类型表达式。如{?}, {? extends classA}, {? super classB}
 * 	- TypeVariable			类型变量，如E
 * 	- GenericArrayType		泛型数组，元素是ParameterizedType或TypeVariable类型。如E[]，List<E>[]
 * 
 * @author nerv
 *
 */
public class Type_ {
	Method m;
	{
		try {
			m = TypeTest.class.getMethod("m1", List.class, List.class, List.class, List.class, List.class, Map.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 泛型<>5种情况，对应Class和4个Type接口
	 */
	@Test
	public void test1() {
		try {
			for (Type paramType: m.getGenericParameterTypes()) {
				// 有泛型
				if (paramType instanceof ParameterizedType) {
					ParameterizedType paramType0 = (ParameterizedType) paramType;
					// 取泛型（脱掉最外层<>）
					System.out.println("==>");
					for (Type argType: paramType0.getActualTypeArguments()) {
						System.out.println(" * " + argType.getTypeName() + "\t" + argType.getClass().getSimpleName());
						// 按类型解析
						if (argType instanceof WildcardType) {
							wildcardType((WildcardType) argType);
						} else if (argType instanceof ParameterizedType) {
							parameterType((ParameterizedType) argType);
						} else if (argType instanceof TypeVariable) {
							typeVariable((TypeVariable<?>) argType);
						} else if (argType instanceof GenericArrayType) {
							genericArrayType((GenericArrayType) argType);
						}
					}
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * WildcardType
	 * 		Type[] getUpperBounds()：泛型上限，只有一个，默认Object
	 * 		Type[] getLowerBounds()：泛型下限，只有一个或没有，默认null
	 * 通配符表达式只有一个上届/下届，定义类型变量时可以指定多个上届
	 * @throws Exception 
	 */
	private void wildcardType(WildcardType type) {
		Type[] uppers = type.getUpperBounds();
		Type[] lowers = type.getLowerBounds();
		System.out.println("\t泛型表达式上界：" + uppers[0]
			+ "，下界：" + (lowers.length > 0 ? lowers[0] : "无"));
	}
	
	/**
	 * ParameterizedType
	 * 		Type getRawType()		声明此类型的类或接口（<>前面的类）
	 * 		Type getOwnerType()		getRawType()的外部类
	 * 		Tyep[] getActualTypeArguments()	取泛型<>中的内容（脱掉最外层<>），ParameterizedType优先
	 * @param type
	 */
	private void parameterType(ParameterizedType type) {
		System.out.println("\trawType：" + type.getRawType());
		System.out.println("\townerType：" + type.getOwnerType());
	}
	
	/**
	 * TypeVariable
	 * 		<D extends GenericDeclaration> getGenericDeclaration()	获取声明该类型变量实体(是Class, Constructor, Method)
	 * 		getName()		在源码中定义的变量名，如E
	 * @param type
	 */
	private void typeVariable(TypeVariable<?> type) {
		System.out.println("\tgenericDeclaration：" + type.getGenericDeclaration());
		System.out.println("\tname：" + type.getName());
		System.out.println("\ttypeName：" + type.getTypeName());
	}
	
	/**
	 * GenericArrayType
	 * 		Type getGenericComponentType()		数组元素的类型（脱掉最外层[]）
	 * @param type
	 */
	private void genericArrayType(GenericArrayType type) {
		System.out.println("\tgenericComponentType：" + type.getGenericComponentType());
	}
	
	/**
	 * 内部接口，用于测试
	 * @author nerv
	 *
	 */
	private interface TypeTest {
		<E extends Map & Cloneable & Serializable> E m1(
				List<ArrayList> a1, 	// 没有<>则为Class
				List<E> a2, 			// TypeVariable
				List<?> a3, 			// WildcardType
				List<ArrayList<E>[]> a4,			// GenericArrayType
				List<Map.Entry<E, E>> a5,	// ParameterizedType
				Map<E, Vector<E>> a6	// ParameterizedType,TypeVariable   注意顺序ParameterizedType优先
		);
	}
}

