package com.dh.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.dh.dao.ProductDAO;
import com.dh.db.DBUtils;
import com.dh.model.Category;
import com.dh.model.Order;
import com.dh.model.PageBean;
import com.dh.model.Product;

public class ProductService {

	
	/**
	 * @Title: listHotProduct 
	 * @Description: TODO(最热商品) 
	 * @param @return
	 * @param @throws Exception  参数说明 
	 * @return List<Product>    返回类型 
	 * @throws 
	 */
	public List<Product> listHotProduct() throws Exception{
		ProductDAO dao=new ProductDAO();
		List<Product> hotProduct=null;
		try {
			hotProduct= dao.listHotProduct();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("查询出错!");
		}
		return hotProduct;
	}
	
	/**
	 * @Title: listNewProduct 
	 * @Description: TODO(最新商品) 
	 * @param @return
	 * @param @throws Exception  参数说明 
	 * @return List<Product>    返回类型 
	 * @throws 
	 */
	public List<Product> listNewProduct() throws Exception{
		ProductDAO dao=new ProductDAO();
		List<Product> newProduct=null;
		try {
			newProduct=dao.listNewProduct();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("查询出错!");
		}
		return newProduct;
	}
	
	/**
	 * @Title: listCategory 
	 * @Description: TODO(种类) 
	 * @param @return
	 * @param @throws Exception  参数说明 
	 * @return List<Category>    返回类型 
	 * @throws 
	 */
	public List<Category> listCategory() throws Exception{
		ProductDAO dao=new ProductDAO();
		List<Category> categoryList=null;
		try {
			categoryList=dao.listCategory();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("查询出错！");
		}
		return categoryList;
	}
	
	public PageBean listProductByCid(String cid,int currentPage,int currentCount) throws Exception {
		ProductDAO dao=new ProductDAO();
		
		//封装一个PageBean 返回web层
		PageBean<Product> pageBean=new PageBean<Product>();
		
		//1、封装当前页
		pageBean.setCurrentPage(currentPage);
		//2、封装每页显示的条数
		pageBean.setCurrentCount(currentCount);
		//3、封装总条数
		int totalCount=0;
		try {
			totalCount=dao.getCount(cid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("查询出错！");
		}
		pageBean.setTotalCount(totalCount);
		//4、封装总页数
		int totalPage=(int)Math.ceil(1.0*totalCount/currentCount);
		pageBean.setTotalPage(totalPage);
		
		//5、当前页显示的数据
		int index=(currentPage-1)*currentCount;
		List<Product> list=null;
		try {	
				list=dao.listProductByPage(cid, index, currentCount);
			}catch(SQLException e) {
				e.printStackTrace();
				throw new Exception("查询出错！");
			}
		
		pageBean.setList(list);	
		return pageBean;
		
	}
		
		
	public Product findProductByPid(String pid) throws Exception {
		ProductDAO dao=new ProductDAO();
		Product product=null;
		try {
			product=dao.findProductByPid(pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("查询出错！");
		}
		return product;
	}

	public void submitOrder(Order order) throws Exception {
		// TODO Auto-generated method stub
		ProductDAO dao=new ProductDAO();
		try {
			//开启事务
			DBUtils.startTransaction();
			
			dao.addOrders(order);
			
			dao.addOrderItem(order);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			DBUtils.rollback();
			e.printStackTrace();
			throw new Exception("提交出错！");
		}finally {
			DBUtils.commitAndRelease();
		}
	}

	public void updateOrderAddress(Order order) throws Exception {
		// TODO Auto-generated method stub
		ProductDAO dao=new ProductDAO();
		try {
			dao.updateOrderAddress(order);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("更新信息出错");
		}
	}

	public void updateOrderState(String orderid) throws Exception {
		// TODO Auto-generated method stub
		ProductDAO dao=new ProductDAO();
		try {
			dao.updateOrderState(orderid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("更新信息出错");
		}
	}

	public List<Order> findAllOrders(String uid) {
		// TODO Auto-generated method stub
		ProductDAO dao = new ProductDAO();
		List<Order> orderList = null;
		try {
			orderList = dao.findAllOrders(uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderList;
	}

	public List<Map<String, Object>> findAllOrderItemByOid(String oid) {
		// TODO Auto-generated method stub
		ProductDAO dao = new ProductDAO();
		List<Map<String, Object>> mapList = null;
		try {
			mapList = dao.findAllOrderItemByOid(oid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
