package com.javax.xml;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * JDOM解析XML（非javax.xml.*）
 * @author LiZhi
 *
 */
public class JDOM {
	/**
	 * 获取属性列表
	 * @param e
	 */
	private static void getAttList(Element e){
		@SuppressWarnings("unchecked")
		List<Attribute> list = e.getAttributes();
		for(Attribute i : list){
			System.out.println(i.getName() + "=" + i.getValue());
		}
	}
	/**
	 * 递归获取子节点
	 * @param e
	 */
	private static void getChildren(Element e){
		System.out.println(e.getName() + "=" + e.getText());
		@SuppressWarnings("unchecked")
		List<Element> list = e.getChildren();
		for(Element i : list){
			getChildren(i);
		}
	}
	public static void main(String[] args) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build("student.xml");
		Element e = document.getRootElement();
		getAttList(e);
		getChildren(e);
	}

}