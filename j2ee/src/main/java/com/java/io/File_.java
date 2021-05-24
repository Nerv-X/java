package com.java.io;

import java.io.File;
import java.io.IOException;
/**
 * File
 * 
 * 	实例方法
 * 		createNewFile()			新建空文件，父级目录不存在会创建失败
 * 		mkdir()					新建目录，父级目录不存在会创建失败
 * 		mkdirs()				级联新建目录
 * 		exists()				文件或目录是否存在
 * 		isFile()				判断是否为文件
 * 		isDirectory()			判断是否为目录
 * 		getParentFile()			上一级文件
 * 		getPath()				相对路径名
 * 		getAbsolutePath()		绝对路径名
 * 		getName()				对象名称
 * 		delete()				删除对象
 * 		mkdir()					创建空文件夹
 * 		length()				文件字节长度，若不存在则返回0L
 * 		listRoots()				获取计算机全部根目录（Windows磁盘符）
 * 		listFiles()				获取子文件夹或子文件
 * @author nerv
 *
 */
public class File_ {

	public static void main(String[] args) {
		try {
			new File_().create("d:\\1\\b.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建新文件
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean create(String path) throws IOException {
		File file = new File(path);
		// 级联创建父级目录，注意file.mkdirs()会使得file变为目录
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            return file.createNewFile();
        }
        return false;
	}
}
