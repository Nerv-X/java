package com.java.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * =================== HttpURLConnection ===================
 * 		基于HTTP协议，底层以socket实现
 * 		是根据URL的请求协议(此处是http)生成的URLConnection类的子类
 * 
 * 	步骤
 * 		1）HttpURLConnection参数设置HTTP请求头
 * 		2）OutputSteam写入HTTP请求正文
 * 		3）发送请求并返回InputStream用于获取响应信息
 *
 *	HttpURLConnection参数设置（必须在connect()前完成）
 *		setRequestMethod("POST")		请求方式，默认GET
 * 		setDoOutput(true)				是否向HttpURLConnection输出，getOutputStream()前提, 默认false
 * 										post请求参数要放在http正文内，需要true
 * 		setDoInput(true)				是否从HttpURLConnection读入，默认true
 * 		setUseCaches(false)				使用缓存，Post请求禁用，默认true
 * 		setReadTimeout(3000)			读取超时（毫秒），无默认值。或System.setProperty("sun.net.client.defaultConnectTimeout", "3000");
 *  	setConnectTimeout(3000);		连接超时（毫米），无默认值。或System.setProperty("sun.net.client.defaultReadTimeout", "3000");
 *  									如果不设置超时，网络异常可能会导致程序僵死
 *		setInstanceFollowRedirects(true)	设置此 HttpURLConnection实例是否自动执行HTTP重定向，默认true
 * 		setRequestProperty(key, value)
 * 			Content-type
 * 				"application/x-java-serialized-object"		设定传输的内容类型是可序列化的java对象，否则可能java.io.EOFException
 * 			Accept
 * 				同上
 * 			Content-Length			HTTP请求体长度
 * 
 * 	创建连接并写入HTTP请求正文（对outputStream的操作必须在getInputStream()前完成）
 * 		connect()					建立一个与服务器的tcp连接，并不发送http请求。要确保HttpURLConnection参数已设置完毕
 * 		getOutputStream()			获取基础输出流，隐式调用connect()。其后再根据传输的数据类型以此输出流构建相应的具体输出流
 * 									输出流将请求数据存入内存缓冲区
 * 	发送请求并读入响应数据
 * 		设置响应头位置sun包HttpURLConnection：http.parseHTTP(responses, pi, this);
 * 		getInputStream()			将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并返回输入流用于获取响应信息.
 * 									只能读取responseCode=200 时的响应内容，有异常时会直接抛出，无返回值（此时inputStream有值但无法返回）
 * 									既使输出流没有调用close()，也不会再向对象输出流写入任何数据. 要再次发送数据时只能重新创建连接
 * 		getErrorStream()			读取responseCode>=400时的响应内容，此时响应头Content-Type=text/html;charset=UTF-8，故中文乱码
 * 
 * ===========================
 * sun.HttpURLConnection
 * 		请求HTTP：HttpClient.writeRequests(MessageHeader head, PosterOutputStream pos)
 */
public class HttpURLConnection_ {
	
	private HttpURLConnection con;
	{
		// HttpURLConnection是根据URL的请求协议(此处是http)生成的URLConnection类的子类，不能直接构造
		try {
			con = (HttpURLConnection) new URL("http://www.baidu.com").openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试：GET请求访问百度首页
	 * @param args
	 */
    public static void main( String[] args )
    {
    	HttpURLConnection_ o = new HttpURLConnection_();
    	o.setParams();
    	System.out.println(o.connectAndSend());
    }
    
    /**
     * HttpURLConnection参数设置（设置HTTP请求头）
     */
    private void setParams() {
    	con.setDoOutput(true);
    	con.setRequestProperty("Content-type", "application/json");
    	con.setRequestProperty("Accept", "application/xml");
    	// 网络超时
    	con.setReadTimeout(3000);
    	con.setConnectTimeout(3000);
	}
    
    /**
     * 连接传输（设置HTTP请求正文）
     */
    private String connectAndSend() {
    	try {
    		// 获得输出流，隐式connect()
			OutputStream out = con.getOutputStream();
			// 通过输出流对象构建对象输出流对象，以实现输出可序列化的对象。 
			ObjectOutputStream objOut = new ObjectOutputStream(out); 
			// 向对象输出流写出数据，这些数据将存到内存缓冲区中 
			objOut.writeObject(new String("我是测试数据")); 
			// 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream） 
			objOut.flush(); 
			// 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中, 
			// 在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器 
			objOut.close();
			
			// 将内存缓冲区中的HTTP请求数据电文发送到服务端
			// <===注意，实际发送请求的代码段就在这里
			InputStream in = con.getErrorStream() != null ? con.getErrorStream(): con.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String line;
			StringBuilder result = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			in.close();
			reader.close();
			con.disconnect();
			// 访问/eureka/apps，code == -1，原因 ？
			if(con.getResponseCode() >= 400) {
				throw new RuntimeException(result.toString());
			}
			return result.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
}
