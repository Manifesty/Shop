package com.dh.dao;

import java.sql.SQLException;


import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.dh.db.DBPool;
import com.dh.db.DBUtils;
import com.dh.model.User;


public class UserDAO {
	
		public int regist(User user) throws SQLException {
			QueryRunner runner=new QueryRunner(DBPool.getDataSource());
			String sql="insert into user values(?,?,?,?,?,?,?,?,?,?)";
			int update=runner.update(sql,user.getUid(),user.getUsername(),user.getPassword(),
					user.getName(),user.getEmail(),user.getTelephone(),user.getBirthday(),
					user.getSex(),user.getState(),user.getCode());
			return update;
		}
		
		public void active(String activeCode) throws SQLException {
			QueryRunner runner=new QueryRunner(DBPool.getDataSource());
			String sql="update user set state=? where code=?";
			runner.update(sql,1,activeCode);
		}
		
		public long checkUsername(String username) throws SQLException {
			QueryRunner runner =new QueryRunner(DBPool.getDataSource());
			String sql="select count(*) from user where username=?";
			long query=(long)runner.query(sql,new ScalarHandler(),username);
			return query;
		}

		//用户登录的方法
		public User login(String username, String password) throws SQLException {
			QueryRunner runner = new QueryRunner(DBPool.getDataSource());
			String sql = "select * from user where username=? and password=?";
			return runner.query(sql, new BeanHandler<User>(User.class), username,password);
		}
		
		
		
		
		
		
		
		
}
