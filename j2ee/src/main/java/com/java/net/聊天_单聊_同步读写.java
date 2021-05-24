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
 * 同步：阻塞收发信息，必需收到信息后才能发送信息
 * 
 * @author nerv
 *
 */
public class 聊天_单聊_同步读写 {

	/**
	 * Socket服务端阻塞接收Socket客户端
	 * @throws IOException
	 */
	@Test
	public void server() throws IOException {
		try (
			Scanner sc = new Scanner(System.in);
			ServerSocket server = new ServerSocket(12306);// 1.创建服务端Scoket
			Socket socket = server.accept(); // 2.阻塞，接收客户端Socket
		) {
			// 3.创建输入输出流
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			while(true){
				System.out.println("【服务端】接收：" + dis.readUTF());//读取信息
				System.out.println("【服务端】发送：");
				dos.writeUTF(sc.next());//发送信息
			}
		}
	}
	
	/**
	 * Socket客户端连接到指定服务端
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	@Test
	public void client() throws UnknownHostException, IOException {
		try (
				// 1.创建Socket,IP地址、端口
				Scanner sc = new Scanner(System.in);
				Socket socket = new Socket("127.0.0.1", 12306)
		){
			// 2.创建输入输出流
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			while(true){
				System.out.println("【客户端】发送：");
				dos.writeUTF(sc.next());//发送
				System.out.println("【客户端】接收：" + dis.readUTF());//接收
			}
		}
	}
	
}

