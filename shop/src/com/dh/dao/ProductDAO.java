package com.dh.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.dh.model.Category;
import com.dh.model.Order;
import com.dh.model.OrderItem;
import com.dh.model.Product;
import com.dh.utils.CommonUtils;
import com.dh.db.DBPool;
import com.dh.db.DBUtils;


public class ProductDAO {

	public List<Product> listHotProduct() throws SQLException{
		QueryRunner runner=new QueryRunner(DBPool.getDataSource());
		String sql="select * from product where is_hot=? limit ?,?";
		return runner.query(sql, new BeanListHandler<Product>(Product.class),1,0,9);
	}
	
	public List<Product> listNewProduct() throws SQLException{
		QueryRunner runner=new QueryRunner(DBPool.getDataSource());
		String sql="select * from product order by pdate desc limit ?,?";
		return runner.query(sql,new BeanListHandler<Product>(Product.class),0,9);
	}
	
	public List<Category> listCategory() throws SQLException{
		QueryRunner runner=new QueryRunner(DBPool.getDataSource());
		String sql="select * from category";
		return runner.query(sql, new BeanListHandler<Category>(Category.class));
	}
	
	public int getCount(String cid) throws SQLException {
		QueryRunner runner=new QueryRunner(DBPool.getDataSource());
		String sql="select count(*) from product where cid=?";
		Long query = (Long) runner.query(sql, new ScalarHandler(),cid);
		return query.intValue();
	}
	
	public List<Product> listProductByPage(String cid,int index,int currentCount) throws SQLException{
		QueryRunner runner=new QueryRunner(DBPool.getDataSource());
		String sql="select * from product where cid=? limit ?,?";
		List<Product> list=runner.query(sql, new BeanListHandler<Product>(Product.class),cid,index,currentCount);
		return list;
	}
	
	public Product findProductByPid(String pid) throws SQLException {
		QueryRunner runner=new QueryRunner(DBPool.getDataSource());
		String sql="select * from product where pid=?";
		return runner.query(sql, new BeanHandler<Product>(Product.class),pid);
	}

	public void addOrders(Order order) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner=new QueryRunner();
		String sql="insert into orders values(?,?,?,?,?,?,?,?)";
		Connection conn=DBUtils.getConnection();
		runner.update(conn, sql, order.getOid(),order.getOrdertime(),order.getTotal(),order.getState(),
				order.getAddress(),order.getName(),order.getTelephone(),order.getUser().getUid());
	}

	public void addOrderItem(Order order) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner=new QueryRunner();
		String sql="insert into orderitem values(?,?,?,?,?)";
		Connection conn=DBUtils.getConnection();
		List<OrderItem> orderItems=order.getOrderItems();
		for(OrderItem item:orderItems) {
			runner.update(conn, sql, item.getItemid(),item.getCount(),item.getSubtotal(),item.getProduct().getPid(),item.getOrder().getOid());
			
		}
	}

	public void updateOrderAddress(Order order) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner=new QueryRunner(DBPool.getDataSource());
		String sql="update order set address=?,name=?,telephone=? where oid=?";
		runner.update(sql,order.getAddress(),order.getName(),order.getTelephone(),order.getOid());
	}

	public void updateOrderState(String orderid) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner=new QueryRunner(DBPool.getDataSource());
		String sql="update order set state=? where oid=?";
		runner.update(sql,1,orderid);
	}

	public List<Order> findAllOrders(String uid) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DBPool.getDataSource());
		String sql = "select * from orders where uid=?";
		return runner.query(sql, new BeanListHandler<Order>(Order.class), uid);
	}

	public List<Map<String, Object>> findAllOrderItemByOid(String oid) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner(DBPool.getDataSource());
		String sql = "select i.count,i.subtotal,p.pimage,p.pname,p.shop_price from orderitem i,product p where i.pid=p.pid and i.oid=?";
		List<Map<String, Object>> mapList = runner.query(sql, new MapListHandler(), oid);
		return mapList;
	}
	
	
	
	
}
