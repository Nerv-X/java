package com.sun.misc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64 {

	// 加密
    public static String getBase64(String str) {
        try {
			return new BASE64Encoder().encode(str.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return null;
    }
	// 解密
    public static String getFromBase64(String s) {
        try {
			return new String(new BASE64Decoder().decodeBuffer(s), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
    
	public static void main(String[] args) {
		System.out.println(getBase64("hr-api:w1p3W6VNXkH4y33K"));
		System.out.println(getFromBase64("aHItYXBpOncxcDNXNlZOWGtINHkzM0s="));
	}


}