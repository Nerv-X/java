package com.javax.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOM解析XML
 * @author LiZhi
 *
 */
public class Dom {
	//获取元素属性
	private static void getAttList(Element e){
		NamedNodeMap map = e.getAttributes();
		for(int i=0; i < map.getLength(); i++)
			System.out.println(map.item(i).getNodeName() + "=" + map.item(i).getNodeValue());
	}
	//获取子节点
	private static void getChild(Node n){
		System.out.println(n.getNodeName() + "=" + n.getTextContent());
		NodeList list = n.getChildNodes();
		for(int i=0; i<list.getLength(); i++){
			//3-换行 8-注释 1-节点
			if(list.item(i).getNodeType() != 3 && list.item(i).getNodeType() != 8){
				getChild(list.item(i));
			}
		}	
	}
	public static void main(String[] args) throws Exception {
		//1.创建解析器工厂对象，根据本地平台默认安装的解析器，自动创建一个工厂的对象并返回
		// 默认System.getProperty("javax.xml.parsers.DocumentBuilderFactory")
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//2.创建解析器对象
		DocumentBuilder builder = factory.newDocumentBuilder();
		//3.解析xml（加载xml文件到内存）
		Document document = builder.parse("Student.xml");
		//4.获取根节点
		Element e = document.getDocumentElement();
		//获取节点属性
		getAttList(e);
		//获取子节点
		getChild(e);
	}
}
