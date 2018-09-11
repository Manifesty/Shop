package com.dh.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;


public class DBUtils {
	
		private static ThreadLocal<Connection> t=new ThreadLocal<Connection>();
		
		//获取连接池
		public static DBPool getDBPool() {
			return DBPool.getInstance();
		}
		
		//获取连接对象
		public static Connection getConnection() {
			Connection conn=t.get();
			if(conn==null) {
				conn=DBPool.getInstance().getConnection();
				t.set(conn);
			}
			return conn;
		}
		
		//开启事务
		public static void startTransaction() throws SQLException {
			Connection conn=getConnection();
			if(conn!=null) {
				conn.setAutoCommit(false);
			}
		}
		
		//事务回滚
		public static void rollback() throws SQLException {
			Connection conn=getConnection();
			if(conn!=null) {
				conn.rollback();
			}
		}
		
		//提交并且 关闭资源及从ThreadLocal中释放
		public static void commitAndRelease() throws SQLException {
			Connection conn = getConnection();
			if (conn != null) {
				conn.commit(); // 事务提交
				conn.close();// 关闭资源
				t.remove();// 从线程绑定中移除
			}
		}
		
		//关闭资源
		public static void closeConnection() throws SQLException {
			Connection con = getConnection();
			if (con != null) {
				con.close();
			}
		}

		public static void closeStatement(Statement st) throws SQLException {
			if (st != null) {
				st.close();
			}
		}

		public static void closeResultSet(ResultSet rs) throws SQLException {
			if (rs != null) {
				rs.close();
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
}
