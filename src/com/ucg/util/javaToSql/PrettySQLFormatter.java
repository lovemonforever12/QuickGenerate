package com.ucg.util.javaToSql;

import org.hibernate.jdbc.util.FormatStyle;

public class PrettySQLFormatter {
	/**
	 * 打印漂亮的SQL语句 韦向阳
	 * 
	 * @since 2011-05-27
	 * @param sql
	 *            SQL语句
	 */
	public static void print(String sql) {
		System.out.println(FormatStyle.BASIC.getFormatter().format(sql));
	}

	/**
	 * 打印漂亮的SQL语句 韦向阳
	 * 
	 * @since 2011-05-27
	 * @param remark
	 *            打印前的说明信息
	 * @param sql
	 *            SQL语句
	 */
	public static void print(String remark, String sql) {
		System.out.println(remark
				+ FormatStyle.BASIC.getFormatter().format(sql));
	}

	/**
	 * 获取漂亮的SQL语句 韦向阳
	 * 
	 * @since 2011-05-27
	 * @param sql
	 *            SQL语句
	 */
	public static String getPerttySql(String sql) {
		return FormatStyle.BASIC.getFormatter().format(sql);
	}

	/**
	 * 获取漂亮的SQL语句 韦向阳
	 * 
	 * @since 2011-05-27
	 * @param remark
	 *            打印前的说明信息
	 * @param sql
	 *            SQL语句
	 */
	public static String getPerttySql(String remark, String sql) {
		return remark + FormatStyle.BASIC.getFormatter().format(sql);
	}
	
	public static void main(String[] args) {
		print("select * from MyUser as A join MyFriend as B on A.id = B.pid where B.name like ? ");
	}
}
