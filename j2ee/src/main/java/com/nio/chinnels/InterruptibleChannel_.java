package com.nio.chinnels;

import java.nio.channels.FileChannel;
import java.nio.channels.InterruptibleChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;



/**
 * InterruptibleChannel：使通道能以异步的方式关闭与中断
 * 	相当于具有Asynchronously和Closeable特性：
 * 		如果有线程阻塞于某可中断的通道，当其他线程调用此通道的close()，则阻塞的线程会收到AsynchronousCloseException
 * 	还具有interruptible特性：
 * 		如果有线程阻塞于某可中断的通道，当其他线程调用此通道的close()，通道会关闭，阻塞的线程会收到ClosedByInterruptException，
 * 		阻塞线程的状态一直是中断的
 * 
 * @author Nerv
 */
public class InterruptibleChannel_ {
	
	InterruptibleChannel channel;

	/**
	 * AbstractInterruptibleChannel：通道支持异步关闭和中断的最基本实现
	 * 		是FileChannel的直接父类
	 * 		在调用可阻塞的IO操作前后，必须分别调用begin()和end()，end()放在finnaly中
	 * 		
	 * 	implCloseChannel()：抽象，释放资源的具体逻辑，会在调用中断方法及close()时调用
	 * 			
	 * 	begin()：初始化中断器Interruptible，如果当前线程是中断状态，则执行Interruptible.interrupt(currentThread)
	 * 		Interruptible.interrupt(target)：标识当前channel为关闭状态，执行implCloseChannel()并记录被中断的线程target
	 * 	end()：异常判断
	 * 		如果当前线程已被中断则抛出ClosedByInterruptException
	 * 		如果当前通道已关闭但completed标识为false则抛出AsynchronousCloseException
	 */
	public void test1() {
		AbstractInterruptibleChannel abstractInterruptibleChannel;
	}
	
}
