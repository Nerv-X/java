package com.nio.chinnels;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.junit.Test;

/**
 * SelectionKey 标识SelectableChannel已经向Selector注册
 * 		cancel()					将此键添加到对应选择器的cancelledKeys，在选择器下一次select()时注销关联的通道并从keys中移除此键
 * 		channel()					返回关联的通道，即使键已取消仍可以返回通道
 * 			channel().close()		关闭通道会执行关联的键的cancel()
 * 		isValid()					是否有效
 * 		isAcceptable()				是否准备好接收新的socket连接，用于服务端
 * 		isConnectable()				通道是否完成socket连接，用于客户端
 * 		isReadable()				通道是否支持并已准备好读取
 * 		isWritable()				通道是否支持并已准备好写入
 * 		
 * @author nerv
 *
 */
public class SelectionKey_ {

	private Runnable server, client;
	private Selector sel;
	{
		try {
			sel = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * isAcceptable()：用于服务端
	 */
	@Test
	public void test1() {
		server = () -> {
			while(true) {
				try {
					sel.select();
					Iterator<SelectionKey> iterator = sel.selectedKeys().iterator();
					while (iterator.hasNext()) {
						SelectionKey key = iterator.next();
						ServerSocketChannel channel = (ServerSocketChannel) key.channel();
						Socket socket = null;
						if (key.isAcceptable()) {
							socket = channel.socket().accept();
							System.out.println("server isAcceptable()");
						}
						socket.close();
						iterator.remove();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		};
		client = () -> {
			try {
				Selector sel2 = Selector.open();
				SocketChannel channel = SelectableChannel_.getClient(sel2);
				while(true) {
					sel2.select();
					Iterator<SelectionKey> iterator = sel2.selectedKeys().iterator();
					while (iterator.hasNext()) {
						SelectionKey key = iterator.next();
						// 通道是否完成socket连接
						if (key.isConnectable()) {
							System.out.println("client isConnectable");
							// 需要finishConnect()完成连接，因为socketChannel是非阻塞模式
							while (!channel.finishConnect()) {
								System.out.println("channel not finishConnect");
							}
							SocketChannel channel2 = (SocketChannel) key.channel();
							channel2.close();
						}
						iterator.remove();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		Selector_.start(server, client);
	}
}
