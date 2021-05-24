/**
 * 在java引入nio之前，对于每个网络连接，都有一个对应的线程来处理网络的读写操作，即使没有可读写的数据，线程仍然阻塞在读写操作上，这样有可能造成资源浪费，而且给操作系统的线程调度也带来压力

	NIO组成：buffer、channel、selector
	
	InputStream/OutputStream：	普通I/O，同步阻塞，当前线程执行读写任务，读不到或写不出去则阻塞等待
	Channel：NIO，同步非阻塞，当前线程执行读写任务，不管读写结果如何都会立即返回，继续执行后续代码
	AsynchronousChannel：AIO，异步，多线程实现读/写操作。A线程发起读操作，B线程实现读。读不到数据时B阻塞，A继续执行
	
	一个I/O操作分两步：
	1）发起I/O请求（调用读写方法）		普通I/O阻塞
	2）实际I/O操作（读写方法的底层实现）	NIO阻塞

 * @author nerv
 *
 */
package com.nio;