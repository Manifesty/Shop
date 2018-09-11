package com.dh.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class DBPool {
		private static DBPool dbPool;
		private static ComboPooledDataSource dataSource;
		
		static {
			dbPool =new DBPool();
		}
		
		public DBPool() {
			try {
				dataSource=new ComboPooledDataSource();
				dataSource.setUser("root");
				dataSource.setPassword("root");
				dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/shop?autoReconnect=true&"
						+ "useUnicode=true&characterEncoding=UTF-8");
				dataSource.setDriverClass("com.mysql.jdbc.Driver");
				dataSource.setInitialPoolSize(5);   //初始化连接池连接数
				dataSource.setMinPoolSize(1);		//连接池最小连接数
				dataSource.setMaxPoolSize(10);		//连接池最大连接数
				dataSource.setMaxStatements(50);	//c3p0全局的PreparedStatements缓存的大小
				dataSource.setMaxIdleTime(60);		//最大空闲时间
			}catch(PropertyVetoException e) {
				throw new RuntimeException(e);
			}
		}
		
		public final static DBPool getInstance() {
			return dbPool;
		}
		
		public final static ComboPooledDataSource getDataSource() {
			return dataSource;
		}
		
		public final Connection getConnection() {
			try {
				return dataSource.getConnection();
			}catch(SQLException e) {
				throw new RuntimeException("无法从数据源获取连接",e);
			}
		}
		
		
		
		
		
		
		
		
}
