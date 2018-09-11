package com.dh.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.dh.dao.AdminDAO;
import com.dh.model.Category;
import com.dh.model.Order;
import com.dh.model.Product;



public class AdminService {

	public List<Category> findAllCategory() {
		// TODO Auto-generated method stub
		AdminDAO dao =new AdminDAO();
		
		try {
			return dao.findAllCategory();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void addProduct(Product product) {
		// TODO Auto-generated method stub
		AdminDAO dao = new AdminDAO();
		try {
			dao.addProduct(product);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Order> findAllOrder() {
		// TODO Auto-generated method stub
		AdminDAO dao = new AdminDAO();
		List<Order> list=null;
		try {
			list = dao.findAllOrder();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> findOrderInfoByOid(String oid) {
		AdminDAO dao = new AdminDAO();
		List<Map<String, Object>> mapList=null;
		try {
			mapList = dao.findOrderInfoByOid(oid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mapList;
	}
	
}
