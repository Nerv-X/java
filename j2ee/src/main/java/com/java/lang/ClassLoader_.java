package com.java.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.Class;
import java.lang.ClassLoader;
import java.lang.ClassNotFoundException;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.System;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

import sun.reflect.Reflection;

/**
 * 动态加载类：byte[]转Class对象
 * 		打破双亲委托模型：重写loadClass()
 * 		类加载器命名空间：每个加载器实例都有单独的命名空间
 * 
 * ClassLoader
 * 	属性
 * 		Vector<Class> classes		所有已加载的类
 * 	方法
 * 		loadClass()			先调用父加载器的loadClass()，失败了我再加载（双亲委派模型）
 * 
 * @author Nerv
 *
 */
public class ClassLoader_ extends ClassLoader {
	
	/** 默认class文件目录 */
	private final static Path DEFAULT_CLASS_DIR = Paths.get("D:\\");
	
	/** class文件目录 */
	private final Path classDir;
	
	
	/**
	 * 构造：默认父加载器AppClassLoader
	 */
	public ClassLoader_() {
		super();
		this.classDir = DEFAULT_CLASS_DIR;
	}
	
	/**
	 * 构造：指定class文件所在磁盘目录
	 * @param classDir
	 */
	public ClassLoader_(String classDir) {
		super();
		this.classDir = Paths.get(classDir);
	}
	
	/**
	 * 构造：指定父加载器
	 * @param parent
	 */
	public ClassLoader_(String classDir, ClassLoader parent) {
		super(parent);
		this.classDir = Paths.get(classDir);
	}
	
	public static void main(String[] args) throws Exception {
		Class<?> c1 = new ClassLoader_("D:\\", null).loadClass("Simple2");
		Class<?> c2 = new ClassLoader_("D:\\", null).loadClass("Simple2");
		System.out.println("两个类加载器实例加载同一类，class对象是否相同：" + (c1 == c2));
	}
	
	/**
	 * 重写loadClass()，以打破双亲委托模型
	 * @param name
	 * @param resolve
	 */
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		System.out.println("-->加载类："+name);
		// 1.根据类的全限定名加锁，确保每个类在多线程下只被加载一次
		synchronized (getClassLoadingLock(name)) {
			// 2.在已加载类的缓存中查看类是否已经加载
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				// 3.1 java/javax开头的类用AppClassLoader系统类加载器加载
				if(!name.startsWith("java.lang.Long")||name.startsWith("java") || name.startsWith("javax")) {
					c = getSystemClassLoader().loadClass(name);
				// 3.2 用自定义类加载器加载
				} else {
					c = this.findClass(name);
				}

				// 4.自定义类加载器无法加载则用父加载器或系统类加载器
				if (c == null) {	
					if(getParent() != null) {
						c = getParent().loadClass(name);
					} else {
						c = getSystemClassLoader().loadClass(name);
					}
				}
			}
			if (c == null) {
				throw new ClassNotFoundException("找不到类 " + name);
			}
			if (resolve) {	//5.加载后是否执行连接阶段
				resolveClass(c);
			}
			return c;
		}
	}
	
	/**
	 * 二字节流解析为Class对象
	 * @param name
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] b = this.getClassBytes(name);
		return this.defineClass(null, b, 0, b.length, null);
	}

	/**
	 * 字节流从磁盘文件读取二进制流
	 * @param name
	 * @return
	 * @throws ClassNotFoundException
	 */
	private byte[] getClassBytes(String name) throws ClassNotFoundException {
		Path fullPath = classDir.resolve(Paths.get(name.replace(".", "/") + ".class"));
		if(!fullPath.toFile().exists()) {
			throw new ClassNotFoundException("找不到类" + name);
		}
		
		byte[] b = null;
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Files.copy(fullPath, out);
			b = out.toByteArray();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
}
