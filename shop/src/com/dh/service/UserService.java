package com.dh.service;

import java.sql.SQLException;

import com.dh.dao.UserDAO;
import com.dh.model.User;

public class UserService {
	
		public boolean regist(User user) {
			UserDAO dao=new UserDAO();
			int row=0;
			try {
				row=dao.regist(user);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return row>0?true:false;
		}
		
		public void active(String activeCode) {
			UserDAO dao=new UserDAO();
			try {
				dao.active(activeCode);
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		public boolean checkUsername(String username) {
			UserDAO dao=new UserDAO();
			long isExist=0L;
			try {
				isExist=dao.checkUsername(username);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return isExist>0?true:false;
		}

		//用户登录的方法
		public User login(String username, String password) throws SQLException {
			UserDAO dao = new UserDAO();
			return dao.login(username,password);
		}
		
		
}
