package com.javax.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Sax解析XML
 * @author nerv
 *
 */
public class Sax {

	public static void main(String[] args)throws Exception {
		//创建sax工厂
		SAXParserFactory factory = SAXParserFactory.newInstance();
		//用sax工厂创建sax对象
		SAXParser sax = factory.newSAXParser();
		//事件驱动，parse()后自动执行DefaultHandler对象中的方法
		sax.parse("Student.xml", new DefaultHandler(){
			@Override
			public void startDocument() throws SAXException {
				System.out.println("开始文档");
			}
			@Override
			public void endDocument() throws SAXException {
				System.out.println("结束文档");
			}
			@Override
			public void startElement(String uri, String localName, String qName,
					Attributes attributes) throws SAXException {
				System.out.println("开始节点");
			}
			@Override
			public void endElement(String uri, String localName, String qName)
					throws SAXException {
				System.out.println("结束节点");
			}

			@Override
			public void characters(char[] ch, int start, int length)
					throws SAXException {
				//super.characters(ch, start, length);
				//System.out.println(ch);
				System.out.println(new String(ch,start,length));
			}
		});
	}

}
