package com.java.io;

/**
 * AutoCloseable强调与try()结合实现自动关闭，针对任何资源，故抛出Exception
 * 		close()不要求幂等，重复调用此接口的close()可能有副作用
 * Closeable作用是关闭I/O流，释放资源，故抛出IOException。继承AutoCloseable，可以自动执行close()也可手动执行
 * 		close()幂等，允许重复调用
 * 
 * @throws Exception
 * @author Nerv
 *
 */
public class Closeable_ {

	
}
