package com.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
/**
 * 服务器端实现远程接口
 * 必须继承UnicastRemoteObject，以允许JVM创建远程的存根/代理。
 * 
 * 
 * RemoteObject：抽象类，实现了Remote接口和序列化Serializable接口，它和它的子类提供RMI服务器函数。
 */
public class RemoteImpl extends UnicastRemoteObject implements Remote_{

	private int numberOfComputations; 
	
	protected RemoteImpl() throws RemoteException { 
		numberOfComputations = 0; 
	} 
	
	@Override 
	public double add(double a, double b) throws RemoteException {
		numberOfComputations++; 
		System.out.println("Number of computations performed so far = " + numberOfComputations); 
		return (a+b); 
	} 
	
	@Override 
	public double subtract(double a, double b) throws RemoteException {
		numberOfComputations++; 
		System.out.println("Number of computations performed so far = " + numberOfComputations); 
		return (a-b); 
	}
	

}
