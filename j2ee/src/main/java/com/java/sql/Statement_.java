package com.java.sql;

import java.sql.*;

/**
 * Statement
 * 		每次执行sql，数据库都要执行sql语句的编译 ，最好用于仅执行一次执行SQL的情景，效率高于PreparedStatement
 * 		参数拼接到SQL中可能导致SQL注入
 * 		executeQuery()			用于查询产生单个结果集
 * 		executeUpdate()			用于执行 INSERT、UPDATE 或 DELETE 语句以及SQL DDL（数据定义语言）语句，如 CREATE TABLE 和 DROP TABLE，返回影响行数
 * 		execute()				返回多个结果集、多个更新计数或二者组合
 * PreparedStatement	继承Statement
 * 		执行SQL命令时，命令会被数据库进行编译和解析，并放到命令缓冲区，然后，每当执行同一个PreparedStatement对象时，由于在缓存区中可以发现预编译的命令，虽然它会被再解析一次，但是不会被再一次编译
 * 		适用多次重复执行和一次批量执行的语句
 * 		参数在SQL以占位符表示，解析时参数值中的引号会被加上\转义避免SQL注入
 * CallableStatement	继承PreparedStatement
 * 		由prepareCall()创建，继承了PreparedStatement处理输入参数的方法，还增加了调用数据库中的存储过程以及设置输出类型参数的功能
 * 		setXX()					设置IN/INOUT参数值
 * 		getXX()					获取OUT/INOUT值（存储过程返回值），Java类型
 * 		registerOutParameter	注册OUT/INOUT参数类型，JDBC类型
 * @author nerv
 *
 */
public class Statement_ {

	private Connection con;
	private Statement stmt;
	private PreparedStatement ps;
	private CallableStatement cs;
	
	/**
	 * Statement
	 * @param id
	 * @param name
	 * @throws SQLException
	 */
	public void test1(int id, String name) throws SQLException {
		stmt = con.createStatement();
		stmt.executeUpdate("insert into user(id, name) values('"+id+"','"+name+"')");
	}
	
	/**
	 * Statement.execute()，不确定返回结果时
	 * @throws SQLException 
	 */
	public void test2() throws SQLException {
		stmt.execute("queryStringWithUnknownResults");
		while (true) {
			int rowCount = stmt.getUpdateCount();
			if (rowCount > 0) { // 它是更新计数
				System.out.println("Rows changed = " + rowCount);
				stmt.getMoreResults();
				continue;
			}
			if (rowCount == 0) { // DDL 命令或 0 个更新
				System.out.println(" No rows changed or statement was DDL command");
				stmt.getMoreResults();
				continue;
			}
			 
			// 执行到这里，证明有一个结果集
			// 或没有其它结果
			 
			ResultSet rs = stmt.getResultSet();
			if (rs != null) {
				//. . . // 使用元数据获得关于结果集列的信息
				while (rs.next()) {
					//. . . // 处理结果
					stmt.getMoreResults();
					continue;
				}
			}
			break; // 没有其它结果
		}
	}
	
	/**
	 * PreparedStatement
	 * @param id
	 * @param name
	 * @throws SQLException
	 */
	public void test2(int id, String name) throws SQLException {
		ps = con.prepareStatement("insert into user(id, name) values(?,?)");
		ps.setInt(1, id);
		ps.setString(2, name);
		ps.executeUpdate();
	}
	
	/**
	 * CallableStatement	调用存储过程
	 * 		setXX()					设置IN/INOUT参数值
	 * 		getXX()					获取OUT/INOUT值（存储过程返回值），Java类型
	 * 		registerOutParameter	注册OUT/INOUT参数类型，JDBC类型
	 * @throws SQLException
	 */
	public void test3() throws SQLException {
		// 1.OUT参数
		cs = con.prepareCall("{call getTestData(?, ?)}");
		cs.registerOutParameter(1, java.sql.Types.TINYINT);
		cs.registerOutParameter(2, java.sql.Types.DECIMAL, 3);
		cs.executeQuery();
		System.out.println("存储过程返回值：" + cs.getByte(1) + "," + cs.getBigDecimal(2));
		// 2.INOUT参数
		cs = con.prepareCall("{call reviseTotal(?)}");
		cs.setInt(1, 25);
		cs.registerOutParameter(1, java.sql.Types.TINYINT);
		cs.executeUpdate();
		System.out.println("存储过程返回值：" + cs.getInt(1));
	}
}
