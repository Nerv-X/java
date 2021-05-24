package com.java.lang;
/**
 * Long
 * 	实例方法
 * 		toString(long, radix) 	按照指定进制显示long的无符号数值，负数会在前面加-
 * 								Long.MIN_VALUE二进制64位，加符号-，共65个字符。底层用char[65]存储字符
 * 		getChars(long, index, char[])	将long转十进制字符数组（大量使用移位运算)
 * 								将Long拆分为int处理（JVM处理int效率高于long）
 * 								乘法配合移位运算代替除法（除法性能低）
 * 参考资料	没看完呢！
 * 	https://blog.csdn.net/songylwq/article/details/9014611
 * 	https://blog.csdn.net/weixin_34194087/article/details/92397070
 * 	https://blog.csdn.net/songylwq/article/details/9015581
 * @author nerv
 *
 */
public class Long_ {
	
}
