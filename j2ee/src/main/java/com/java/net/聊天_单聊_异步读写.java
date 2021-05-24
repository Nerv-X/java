package com.java.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.junit.Test;


/**
 * 单聊：客户端与服务端互相收发消息
 * 异步：接收与发送消息异步进行，不必有先后顺序
 * @author nerv
 *
 */
public class 聊天_单聊_异步读写 {
	
	class ReceiveThread extends Thread{
		private String str = "";
		private Socket socket;
		@Override
		public void run() {
			try {
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				while(true)
					System.out.println(str + dis.readUTF());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public ReceiveThread(String str, Socket socket){
			this.str = str;
			this.socket = socket;
		}
		public ReceiveThread(Socket socket){
			this.socket = socket;
		}
	}

	@Test
	public  void client() throws UnknownHostException, IOException {
		try(
			Scanner sc = new Scanner(System.in);
			Socket socket = new Socket("127.0.0.1", 12306);//1.创建Socket连接
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());//2.创建输入输出流
		){
			//3.子线程循环接收
			new ReceiveThread("【客户端】接收：", socket).start();
			//3.主线程循环发送
			while(true){
				System.out.println("【客户端】发送：");
				dos.writeUTF(sc.next());
			}
		}
	}
	
	@Test
	public void server() throws IOException {
		try(
			Scanner sc = new Scanner(System.in);
			ServerSocket server = new ServerSocket(12306);//1.创建服务端Scoket
			Socket socket = server.accept(); // 2.阻塞接收客户端Socket
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());// 3.创建输出流
		){
			//4.子线程循环接收
			new ReceiveThread("【服务端】接收：", socket).start();
			//5.主线程循环发送
			while(true){
				System.out.println("【服务端】发送：");
				dos.writeUTF(sc.next());
			}
		}
	}
}
