package com.java.io;

import java.io.Serializable;

/**
 * Serializable	序列化
 * 		为了解决对象反序列化的兼容性问题。
 * 		即序列化后，类结构被修改，如何判断反序列化的对象是否为修改后的类的对象
 * 	serialVersionUID
 * 		不指定值，则根据类名、接口名、成员方法以及属性等生成一个64位的哈希值隐式赋值
 * 		对象反序列化时，根据类名及serialVersionUID判断是哪个类的对象
 * 		以上方式只能恢复成Java对象，如果想要恢复成其他对象(如C++对象)，那就要将Java对象转换为XML格式，
 * 		这样可以使其被各种平台和各种语言使用。可以使用随JDK一起发布的javax.xam.*类库，或者使用开源XOM类库
 * @author nerv
 *
 */
public class Serializable_ implements Serializable {

}
