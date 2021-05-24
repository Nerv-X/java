package com.javax.xml;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * DOM4J，是JDOM的分支（非javax.xml.*）
 * @author LiZhi
 */
public class DOM4J {

	private static void getAttList(Element e){
		@SuppressWarnings("unchecked")
		List<Attribute> list = e.attributes();
		for(Attribute i : list){
			System.out.println(i.getName() + "=" + i.getValue());
		}
	}
	private static void getChildren(Element e){
		System.out.println(e.getName() + "=" + e.getText());
		@SuppressWarnings("unchecked")
		List<Element> list = e.elements();
		for(Element i : list){
			getChildren(i);
		}
	}
	public static void main(String[] args) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read("student.xml");
		Element e = document.getRootElement();
		getAttList(e);
		getChildren(e);
	}

}
