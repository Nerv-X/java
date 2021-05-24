package com.java.sql;

import java.sql.Types;
/**
 * Types	JDBC类型
 * 		因为同一SQL数据类型在不同数据库中的名称可能不同，故定义一系列的常规SQL数据类型标识符统一表示
 * 		编写可移植到各种数据库 的JDBC 程序，主要有两方案
 * 			1）限制自己只使用那些被广为接受的 SQL 类型名称（例如 INTEGER、NUMERIC 或VARCHAR）
 * 			2）用 java.sql.DatabaseMetaData.getTypeInfo()找出给定的数据库实际上支持哪些 SQL类型，然后选择与给定 JDBC 类型相匹配的特定于数据库的 SQL 类型名。
 * 
 * 1.字符类型
 * 
 * 	CHAR	固定长度的小字符串，空格填充
 * 		示例
 * 			CHAR(12) 	// 12 个字符的字符串
 * 		SQL类型
 * 			SQL-92定义的CHAR，主流数据库都实现。最大长度至少254 个字符
 * 		Java类型
 * 			String 或 char[]		// resultSet.getString()
 * 
 * 	VARCHAR	表示长度可变的小字符串
 * 		示例
 * 			VARCHAR(12) 	//	12 个字符的字符串
 * 		SQL类型
 * 			SQL-92定义的VARCHAR，主流数据库实现。最大长度至少254 个字符。赋值时，数据库会保存字符串的长度，确保 SELECT返回准确的原始字符串
 * 		Java类型
 * 			String 或 char[]		// resultSet.getString() 
 * 
 * 	LONGVARCHAR	长度可变的大字符串
 * 		SQL类型
 * 			非标准的 SQL扩展，主流数据库都有实现，但名称不同。通常在 254 个字节以内
 * 		Java类型
 * 			String 或 char[]		// resultSet.getString() 或 getCharacterStream()/getAsciiStream()获取unicode/ascii字符流
 * 
 * 2.字节类型
 * 
 * 	BINARY 		固定长度的小二进制值
 * 		示例
 * 			BINARY(12) 	// 12 个字节的 binary 类型
 * 		SQL类型
 * 			非标准的 SQL扩展，部分数据库实现。通常在 254 个字节以内
 * 		Java类型
 * 			byte[]		// resultSet.getBytes()
 * 
 * 	VARBINARY 	长度可变化的小二进制值
 * 		示例
 * 			VARBINARY(12)  // 长度最大 12 个字节的二进制类型
 * 		SQL类型
 * 			非标准的 SQL扩展，部分数据库实现。通常在 254 个字节以内。变量赋值时，数据库会保存值的长度，确保SELEC返回准确的原始值
 * 		Java类型
 * 			byte[]		// resultSet.getBytes()
 * 
 * 	LONGVARBINARY 	表示长度可变化的大二进制值
 * 		SQL类型
 * 			非标准的 SQL扩展，主流数据库都支持，但名称不同
 * 		Java类型
 * 			byte[]		// resultSet.getBytes() 或 getBinaryStream() 数据很大时最好以字节流读取
 * 
 * 3.数值类型
 * 
 * 	 BIT		位值（0 或 1）
 * 		SQL类型
 * 			SQL-92 定义的BIT，部分数据库实现。基于可移植性可用 JDBC SMALLINT 类型，这种类型已得到广泛支持
 * 		Java类型
 * 			boolean		// resultSet.getBoolean()
 * 
 * 	TINYINT		8位无符号整数（0~255）
 * 		SQL类型
 * 			 TINYINT，部分数据库实现。基于可移植性可用 JDBC SMALLINT 类型，这种类型已得到广泛支持
 * 		Java类型
 * 			byte或short	// byte有符号（-128~127）不能表示TINYINT全部值，short可以
 * 	
 * 	SMALLINT	16 位有符号整数（-32768~32767）
 * 		SQL类型
 * 			 SQL-92定义的SMALLINT，主流数据库都支持。SQL-92 标准未规定其精度。主流数据库都至少支持16位
 * 		Java类型
 * 			short		// resultSet.getShort()
 * 
 * 	INTEGER		32 位有符号整数（-2147483648~2147483647）
 * 		SQL类型
 * 			 SQL-92定义的INTEGER，主流数据库都支持。SQL-92 标准未规定其精度。主流数据库都至少支持32位
 * 		Java类型
 * 			int			// resultSet.getInt()
 * 
 * 	BIGINT		64 位有符号整数（-9223372036854775808 ~ 9223372036854775807）
 * 		SQL类型
 * 			SQL的非标准扩展，没有数据库实现
 * 		Java类型
 * 			long		// resultSet.getLong()
 * 
 * 	REAL	有7位尾数的“单精度”浮点数
 * 		SQL类型
 * 			 SQL-92定义的REAL，主流数据库都支持。SQL-92 标准未规定其精度。主流数据库都至少支持7位尾数精度
 * 		Java类型
 * 			float		// resultSet.getFloat()
 * 
 * 	DOUBLE 有15位尾数的“双精度”浮点数
 * 		SQL类型
 * 			 SQL-92定义的DOUBLE PRECISION，主流数据库都支持。SQL-92 标准未规定其精度。主流数据库都至少支持15位尾数精度
 * 		Java类型
 * 			double		// resultSet.getDouble()
 * 
 * 	DECIMAL、NUMERIC		固定精度的十进制值
 * 		示例
 * 			DECIMAL(5,3)	// 5位精度、3位比例，可表示12.345
 * 			NUMERIC(2,2)	// 2位精度、2位比例，可表示.11
 * 		SQL类型
 * 			SQL-92定义的DECIMAL和NUMERIC，带有精度（总位数）和比例参数（小数位数）
 * 		Java类型
 * 			java.math.BigDecimal	// resultSet.getBigDecimal()
 * 
 * 4.时间类型
 * 		java.util.Date含有 DATE 和 TIME 的信息但不含纳秒信息，因此 JDBC定义了三个 java.util.Date 的子类与 SQL 类型对应
 * 		国际化 (internationalization) 方法一般将 java.util.Date 对象用作变量，精确到纳秒时则用TIMESTAMP
 * 
 * 	DATE	年、月、日组成的日期
 * 		SQL类型
 * 			 SQL-92定义的DATE，部分数据库实现，某些数据库提供了类似语义的 SQL类型
 * 		Java类型
 * 			java.sql.Date		// java.util.Date基本类中的小时、分钟和秒都设为 0
 * 
 * 	TIME	小时、分钟和秒组成的时间，精确到毫秒
 * 		SQL类型
 * 			 SQL-92定义的TIME，部分数据库实现，某些数据库提供了类似语义的 SQL类型
 * 		Java类型
 * 			java.sql.Time		// java.util.Date的年、月、日域设为 1970 年 1 月 1 日。这是 Java 纪元的“零”日期
 * 
 * 	TIMESTAMP	DATE 加上 TIME，精确到纳秒。对应的 TIMESTAMP 类型，其定义由 SQL-92 给出，但只有少数几个数据库实现它。
 * 		SQL类型
 * 			 SQL-92定义的TIMESTAMP，部分数据库实现
 * 		Java类型
 * 			java.sql.Timestamp		// 扩展了java.util.Date添加了纳秒域
 * @author nerv
 *
 */
@SuppressWarnings("unused")
public class Types_ {

}
