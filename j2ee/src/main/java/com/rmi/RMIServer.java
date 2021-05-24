package com.rmi;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMI服务端，在rmiregistry中注册
 */ 
public class RMIServer { 
	 public static void main(String[] args) { 
		 try {
			 // 服务端有多个网络接口时需要显式指定注册的地址
			 System.setProperty("java.rmi.server.hostname","192.168.2.143");
			 System.out.println("远程代理信息：" + UnicastRemoteObject.exportObject(new Remote(){},0));
			 // 注册远程对象,向客户端提供远程对象服务。
			 // 远程对象是在远程服务上创建的，你无法确切地知道远程服务器上的对象的名称， 
			 // 但是,将远程对象注册到RMI Registry之后, 
			 // 客户端就可以通过RMI Registry请求到该远程服务对象的stub，利用stub代理就可以访问远程服务对象了。 
			 RemoteImpl remoteObject = new RemoteImpl();
			 // 创建并导出接受指定端口请求的本地主机上的 Registry 实例
			 LocateRegistry.createRegistry(1099);
			 Registry registry = LocateRegistry.getRegistry();
			 registry.bind("Compute", remoteObject); 
			 System.out.println("Server ready");
			 // 如果不想再让该对象被继续调用，使用下面一行
			 // UnicastRemoteObject.unexportObject(remoteObject, false); 
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }
 }

