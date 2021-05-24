package com.java.lang;

import java.lang.Exception;
import java.lang.RuntimeException;
import java.lang.SuppressWarnings;
import java.lang.Throwable;

import org.junit.Test;
/**
 * 异常专题
 * @author Administrator
 *
 */
public class Throwable_ {

	@SuppressWarnings("unchecked")
	private <E extends Throwable> void throwException(Throwable t) throws E {
		throw (E) t;
	}
	
	/**
	 * 抛出受检异常而不报错：以泛型欺骗编译器
	 */
	@Test
	public void test1() {
		this.<RuntimeException>throwException(new Exception("抛出受检异常"));
	}
}
