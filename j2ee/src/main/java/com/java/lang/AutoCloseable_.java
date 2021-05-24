package com.java.lang;

import java.lang.AutoCloseable;
import java.lang.Exception;
import java.lang.Override;
import java.lang.System;

import org.junit.Test;


/**
 * AutoCloseable与try()结合实现自动关闭
 * 		try()内最后一条语句无分号，且变量都要实现AutoCloseable接口
 * 		try()内变量在try{}结束时自动执行close()
		try()内多个变量的释放顺序：先声明后释放
 * @author Nerv
 *
 */
public class AutoCloseable_ {

	/**
	 * 正常情况
	 * @throws Exception
	 */
	@Test
	public void test1() throws Exception {
		try (Test1 t = new Test1()) {
			System.out.println("执行try块");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("执行finally块");
		}
	}
	
	/**
	 * 异常情况：先执行close()再进入catch块
	 * @throws Exception
	 */
	@Test
	public void test2() throws Exception {
		try (Test1 t = new Test1()) {
			System.out.println("执行try块");
			if(1/0 == 0);
		} catch (Exception e) {
			System.out.println("发生异常");
		} finally {
			System.out.println("执行finally块");
		}
	}
	
	class Test1 implements AutoCloseable {

		@Override
		public void close() throws Exception {
			System.out.println("执行close()");
		}
		
	}
}
