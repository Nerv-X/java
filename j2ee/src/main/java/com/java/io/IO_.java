package com.java.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

/**
 * IO流
 * 	
 * 		汉字：UTF-8 三个字节，GBK两个字节
 * 		回车为\r\n占两个字节
 * 		字符流底层以字节流实现
 * 	InputStream/OutputStream					字节流抽象类
 * 		BufferedInputStream/BufferedOutputStream	缓冲流
 * 		FileInputStream/FileOutputStream			文件流
 * 		DataInputStream/DataOutputStream			数据流	<阻塞>
 * 		ZipInputStream/ZipOutputStream				解压/压缩流
 * 		ZipFile										解压流，需要手动指定entry名称
 * 		ObjectInputStream/ObjectOutputStream		对象流（序列化/反序列化）
 * 		===============以下为其它包扩展的IO流==============
 * 		SocketInputStream/SocketOutputStream		Socket通信读写流
 * 	Reader/Writer								字符流抽象类
 * 		BufferedReader/BufferedWriter				缓冲流
 * 		InputStreamReader/OutputStreamWriter		转换流
 * 		FileReader/FileWriter						文件流
 * 
 * 	
 * @author nerv
 *
 */
public class IO_ implements Serializable {

	private static final long serialVersionUID = -3706458334216424412L;
	private final String PATH_IN = "d:\\2.txt";
	private final String PATH_OUT = "d:\\b.txt";
	
	/**
	 * 乱码测试：
	 * 	
	 * 	定理
	 * 		对给定字符，以任意支持该字符的字符集编码，再以同一字符集解码，可恢复字符（即不乱码）
	 * 	推论
	 * 		如果解码与编码的字符集不同，或字符集不支持字符，则乱码
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void test1() throws UnsupportedEncodingException {
		String s = "abc李智大帝万岁！";
		String charset = "gbk";	// 换成ascii则输出的中文字符全为?
		// 解码与编码使用同一字符集，且支持中文，不乱码
		s = new String(s.getBytes(charset), charset);
		System.out.println(s);
		
	}
	/**
	 * 字节流读写
	 * 	FileInputStream			以输入文件的字符集进行解码
	 * 		available()				可以被读取的字节数
	 * 	FileOutputStream		将byte[]输出到文件并以输出文件的字符集编码
	 * 
	 * 	流转换：输入文件（字符） -①- 字节输入流 -②- 字节输出流 -③- 输出文件（字符）
	 * 		① 使用输入文件的字符集解码，安全
	 * 		② 不涉及字符集，安全
	 * 		③ 如果输入输出文件的字符集不同，【乱码】
	 * @throws IOException 
	 */
	@Test
	public void copyFileByte(){
		try (	// 自动执行close()
			InputStream pis = new FileInputStream(PATH_IN);
			OutputStream pos = new FileOutputStream(PATH_OUT, true)	// 追加
		) {
			byte[] b = new byte[pis.available()];
			pis.read(b);
			// 如果输入文件不是utf-8，则此处乱码，说明系统并非以默认的utf-8解码
			System.out.println(new String(b, "utf-8"));
			pos.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 转换流
	 * 		字符流的底层是以字节流实现，转换流是文件字符流的父类
	 * 	InputStreamReader	以指定或系统默认字符集将字节编码为字符
	 * 		read()				读取一个字符，流尾则-1
	 * 	OutpuStreamWriter	以指定或系统默认字符集将字符解码为字节
	 * 		write(int)			写入一个字符
	 * 		flush()				清空缓冲区，因为多个字节编码为一个字符，故需要缓冲区
	 * @throws IOException 
	 */
	@Test
	public void castStream() throws IOException{
		try (	// 不指定字符集则使用默认字符集
			InputStreamReader reader = new InputStreamReader(new FileInputStream(PATH_IN),"UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(PATH_OUT, true), "UTF-8");
				// 外层使用缓冲流可一次传输多个字符，效率更高
			BufferedReader r = new BufferedReader(reader);
			BufferedWriter w = new BufferedWriter(writer);
		) {
			int result = 0;
			while((result = r.read()) != -1){
				w.write(result);
			}
			w.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 字符流读写
	 * 	FileReader		实际使用InputStreamReader底层封装FileInputStream读取
	 * 		read(char[])		将数据读进char数组
	 * 							返回读取的char数量，即编码单元数量；未读到则-1
	 * 		read(char cbuf[], int off, int len)		读取数据进数组
	 * 		read()				== read(new char[1], 0, 1) 读单个字符
	 * 	FileWriter		实际使用OutputStreamWriter底层封装FileOutputStream写入
	 * 		FileWriter(path, boolean)	是否追加写入数据，默认false
	 * 		write(char[])				写入数据，数组中未赋值的元素写入文件后为null字符
	 * 		write(char[], off, len)		写入数据从数组off开始len个元素
	 * 	流转换：输入文件（字符） -①- 字节输入流 -②- 字符输入流 -③- 字符输出流 -④- 字节输出流 -⑤- 输出文件（字符）
	 * 		① 使用FileInputStream，安全
	 * 		② 使用InputStreamReader，如果系统默认字符集与输入文件不同，【乱码】
	 * 		③ 使用FileReader/FileWriter，无法显式指定字符集，安全
	 * 		④ 使用OutputStreamWriter，如果系统默认字符集与InputStreamReader不同，【乱码】
	 * 		⑤ 使用FileOutputStream，如果OutputStreamWriter的字符集与输出文件不同，【乱码】
	 * 	总结：只有输出文件、输入文件的字符集与系统默认字符集相同，才不会乱码
	 * @throws IOException 
	 */
	@Test
	public void copyFileChar(){
		try(
			Reader r = new FileReader(PATH_IN);
			Writer w = new FileWriter(PATH_OUT, true) 	// 追加内容
		){
			char[] data = new char[1024];
			int count = r.read(data);r.read();
			// 数组中未赋值的元素不要写入
			w.write(data, 0, count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 缓冲流读写
	 * 
	 * 	BufferedReader		提供char[]作为缓冲区，本身不提供读功能，需要传入字符输入流对象
	 * 		readLine()			读取行（遇'\t'、'\r'、return而停），未读到数据则返回null
	 * 	BufferedWriter		提供char[]作为缓冲区，本身无法写，需要传入字符输出流对象
	 * 		write(char[], off, len)		写入字符数组
	 * 		write(String, off, len)		写入字符串
	 * 		flush()			将缓冲区char[]内容输出并清空
	 * 	BufferedOutputStream
	 * 		flush()			将缓冲区byte[]内容以out.write()方式输出并清空
	 * 						OutputStream仅缓冲流实现了此方法，其它输出流都是空方法
	 * @throws IOException 
	 */
	@Test
	public void copyFileBuffered() throws IOException{
		try (
			BufferedReader br = new BufferedReader(new FileReader(PATH_IN));
			BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_OUT, true))	// 追加
		){
			String str = null;
			while((str = br.readLine()) != null)
				bw.write(str + "\r\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 数据流读写
	 * 	DataInputStream
	 * 		read()			阻塞，读取并返回下一个字节数据，流结尾则返回-1
	 * DataOutputStream
	 * 		write(int)		写入一字节数据，计数器+1
	 */
	public void copyImgData() throws IOException{
		try (
			DataInputStream dis = new DataInputStream(new FileInputStream(PATH_IN));
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(PATH_OUT, true));	// 追加
		){
			int result = 0;
			while((result = dis.read())!=-1)
				dos.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 对象流： 序列化ObjectOutputStream，反序列化ObjectInputStream
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void objectStream() {
		File file = new File("d:\\o.txt");
		// 1. 序列化，将java对象转字节序列
		if (!file.exists()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
				//最后一个必须是null，否则java.io.EOFException，因为readObject()返回空对象时!= null会继续执行
				Student[] s = {new Student("张三"),new Student("李四"),new Student("王五"), null};
				for(Student i : s)
					oos.writeObject(i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
		// 2. 反序列化，字节序列转Java对象。通过serialVersionUID判断属于哪个类
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				Student stu = null;
				while((stu = (Student)ois.readObject()) != null)
					System.out.println(stu.getName());
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 压缩单文件或目录
	 * @param zos	压缩流对象
	 * @param inPath 磁盘路径
	 * @param zipPath 压缩文件内部路径，初始为""
	 * @throws IOException 
	 */
	private void putEntry(ZipOutputStream zos, String inPath, String zipPath) throws IOException{
		File file = new File(inPath);
		//1.文件夹则遍历全部子文件，递归压缩
		if(file.isDirectory()){
			File[] files = file.listFiles();
			if(files.length > 0)
				for (File i : files) //File.separator即"\\"
					putEntry(zos, inPath + File.separator + i.getName(), zipPath + file.getName() + "/");
			else{
				//压缩空文件夹最后一定加/不能用\\，（其它电脑可以用\\貌似是设置问题或者冲突）
				zos.putNextEntry(new ZipEntry(zipPath + file.getName() +"/"));
			}
		//2.文件则将其内容写入压缩文件内
		} else{
			zos.putNextEntry(new ZipEntry(zipPath + file.getName()));
			FileInputStream fis = new FileInputStream(inPath);
			int result = 0;
			while((result = fis.read()) != -1)
				zos.write(result);
			fis.close();
		}
	}
	/**
	 * 压缩	ZipOutputStream
	 * 		flush()			清空缓冲区
	 * @param inPath
	 * @param outPath
	 */
	public void zip(String inPath, String outPath) throws IOException{
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outPath));
		putEntry(zos, inPath, "");
		zos.flush();
		zos.close();
	}

	/**
	 * 解压缩 ZipInputStream
	 * @param inPath
	 * @param outDir	输出目录
	 * @exception IOException
	 */
	public void unZip(String inPath, String outDir) throws IOException{
		File inFile = new File(inPath);
		if (!inFile.exists()) {
			return;
		}
		ZipInputStream zis = new ZipInputStream(new FileInputStream(inFile));
		OutputStream fos = null;
		ZipEntry entry = null;
		
		while((entry = zis.getNextEntry()) != null){
			File outFile = new File(outDir + File.separator + entry.getName());
			if(!entry.isDirectory()){
				outFile.getParentFile().mkdirs();	// 创建输出路径中的目录
				fos = new FileOutputStream(outFile);
				int result = 0;
				while((result = zis.read())!= -1)
					fos.write(result);
			} else {
				outFile.mkdirs();
			}
		}
		fos.close();
		zis.close();
	}
	
	
	public static void main(String[] args) throws IOException {
		IO_ t = new IO_();
		//t.unZip("d:\\a.zip", "d:\\temp");
		t.zip("d:\\a", "d:\\a.zip");
	}
	
	class Student implements Serializable{
		private static final long serialVersionUID = 1L;
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Student(String name){
			this.name = name;
		}
		
	}
}
