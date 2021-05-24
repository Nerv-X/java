package com.java.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

/**
 * 群聊
 * 		服务端管理连接的客户端列表，将某一客户端消息向其它客户端转发
 * 		客户端主线程发送数据，子线程异步从服务器读取并展示
 * 
 * @author nerv
 *
 */
public class 聊天_群聊_多线程 {
	
	// 子线程：客户端读取信息
	class ClientReadThread extends Thread{
		private Socket socket;
		public Socket getSocket() {
			return socket;
		}
		public void setSocket(Socket socket) {
			this.socket = socket;
		}
		public void run() {	//从服务器接收其它客户端信息
			try {
				while(true){
					DataInputStream dis = new DataInputStream(socket.getInputStream());
					System.out.println(dis.readUTF());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 服务端：每个线程对应一个Socket客户端的信息同步收发，发消息时会发送给全部
	 */
	@Test
	public void server() {
		List<Socket> list = new ArrayList<>();
		try (ServerSocket server = new ServerSocket(12306)) {
			while(true){	//接收多个客户端，需要循环
				Socket socket = server.accept();
				list.add(socket);
				new Thread(){
					private List<Socket> list;
					private Socket socket;	//当前发送信息的客户端
					@Override
					public void run() {	//读取当前发送信息的数据发送给其它客户端
						try {
							DataInputStream dis;
							String content;
							while(true){
								dis = new DataInputStream(socket.getInputStream());
								content = dis.readUTF();
								//发送其它客户端
								for(Socket i : list)
									if(i != socket){	//不包括自己
										DataOutputStream dos = new DataOutputStream(i.getOutputStream());
										dos.writeUTF(content);
									}
							}
						} catch (SocketException e0) {
							System.out.println("客户端已下线");
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
					public void go(List<Socket> list, Socket socket){
						this.list = list;
						this.socket = socket;
						start();
					}
				}.go(list, socket);
			}
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	/**
	 * 多次运行，以启动多个客户端
	 * 客户端主线程写，子线程读
	 */
	@Test
	public void client() {
		try(
				Scanner sc = new Scanner(System.in);
				Socket socket = new Socket("127.0.0.1", 12306)
		){
			System.out.print("请设置昵称：");
			String name = sc.next();
			ClientReadThread th = new ClientReadThread();
			th.setSocket(socket);
			th.start();
			while(true){
				String content = sc.next();
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF("【"+name+"】：" + content);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
