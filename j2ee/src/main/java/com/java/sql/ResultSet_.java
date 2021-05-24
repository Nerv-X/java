package com.java.sql;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
/**
 * ResultSet
 * 		getXX(int 或 String)			根据不同Java类型获取数据，与JDBC类型映射关系见java.sql.Types_
 * 									getObject()/setObject()会将基本类型隐式转包装类
 * 		getMetaData()	获取全部列的相关信息（名称、SQL类型等）
 * @author nerv
 *
 */
public class ResultSet_ {

	private ResultSet rs;
	
	public void name() throws SQLException {
		InputStream is = rs.getBinaryStream(1);
		rs.getCharacterStream(1);
		rs.getAsciiStream(1);
		rs.getMetaData();ResultSetMetaData m;
	}
}
