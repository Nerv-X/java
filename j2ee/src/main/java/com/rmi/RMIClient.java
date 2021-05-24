package com.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry; 
/**
 * RMI客户端，在Linux运行时需要接口Remote_（可以新建一个也可以将服务端的Remote_导出为jar并在客户端引入）
 * @author Nerv
 *
 */
public class RMIClient { 
	public static void main(String[] args) { 
		try {
			Registry registry = LocateRegistry.getRegistry("192.168.2.143", 1099);
			// 从Registry中检索远程对象的存根/代理
			Remote_ remote = (Remote_)registry.lookup("Compute"); 
			// 调用远程对象的方法 
			double addResult = remote.add(5.0, 3.0); 
			System.out.println("5.0 + 3.0 = " + addResult); 
			double subResult = remote.subtract(5.0, 3.0); 
			System.out.println("5.0 - 3.0 = " + subResult);	
		} catch(Exception e) { 
			e.printStackTrace(); 
		} 
	} 
}