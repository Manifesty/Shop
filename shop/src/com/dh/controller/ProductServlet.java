package com.dh.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.dh.model.Cart;
import com.dh.model.CartItem;
import com.dh.model.Category;
import com.dh.model.Order;
import com.dh.model.OrderItem;
import com.dh.model.PageBean;
import com.dh.model.Product;
import com.dh.model.User;
import com.dh.service.ProductService;
import com.dh.utils.CommonUtils;
import com.dh.utils.PaymentUtil;

import net.sf.json.JSONArray;

/**
 * Servlet implementation class ProductServlet
 */
@WebServlet(
		urlPatterns= {
				"/index.do","/category.do","/productListByCid.do",
				"/productInfo.do","/addProductToCart.do","/delProFromCart.do",
				"/clearCart.do","/privilege/submitOrder.do","/confirmOrder.do","/privilege/myOrder.do"
		}
		)
public class ProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProductServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		String path=request.getRequestURI();
		String methodName=path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));
		try {
			Method method=getClass().getDeclaredMethod(methodName, HttpServletRequest.class,
					HttpServletResponse.class);
			method.invoke(this, request,response);
		}catch(Exception e) {
			e.printStackTrace();
			out.println("没有您访问的页面！");
			response.setHeader("refresh", "2;url=/shop/index.jsp");
		}
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void index(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		ProductService service=new ProductService();
		//热门商品
		try {
			List<Product> hotProduct=service.listHotProduct();
			List<Product> newProduct=service.listNewProduct();
//			List<Category> categoryList=service.listCategory(); 
//			request.setAttribute("categoryList", categoryList);
			request.setAttribute("hotProduct", hotProduct);
			request.setAttribute("newProduct", newProduct);
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println("查询出错！");
			response.setHeader("refresh","2,url=/shop/index.jsp");
		}
		out.flush();
		out.close();
	}
	
	public void category(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		ProductService service=new ProductService();
		try {
			List<Category> categoryList=service.listCategory();
			JSONArray json=JSONArray.fromObject(categoryList);
			out.println(json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println("查询种类出错！");
			response.setHeader("refresh","2,url=/shop/index.jsp");
		}
		out.flush();
		out.close();
	}
	
	//商品分类显示
	public void productListByCid(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		ProductService service=new ProductService();
		String cid=request.getParameter("cid");
		request.setAttribute("cid", cid);
		String currentPageStr=request.getParameter("currentPage");
		int currentCount=12;
		if(currentPageStr==null) {
			currentPageStr="1";
		}
			
		try {
			PageBean<Product> pageBean = service.listProductByCid(cid,Integer.parseInt(currentPageStr), currentCount);
			request.setAttribute("pageBean", pageBean);
			
			//定义一个记录历史商品信息的集合
			List<Product> historyProductList=new ArrayList<Product>();
			
			
			//获取cookie
			Cookie[] cookies=request.getCookies();
			if(cookies!=null) {
				for(Cookie cookie:cookies) {
					if("pids".equals(cookie.getName())) {
						String pids=cookie.getValue();
						String[] spilt=pids.split("-");
						for(String pid:spilt) {
							Product pro=service.findProductByPid(pid);
							historyProductList.add(pro);
						}
					}
				}
			}
			
			//历史记录放入域中
			request.setAttribute("historyProductList", historyProductList);
			
	
			request.getRequestDispatcher("/product_list.jsp").forward(request,response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println("查询出错！");
			response.setHeader("refresh","2,url=/shop/index.jsp");
		}
		out.flush();
		out.close();
	}
	
	//显示商品信息
	public void productInfo(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		ProductService service=new ProductService();
		//获取商品 种类 当前页
		String pid=request.getParameter("pid");
		String cid=request.getParameter("cid");
		String currentPage=request.getParameter("currentPage");
		request.setAttribute("cid",cid);
		request.setAttribute("currentPage", currentPage);
		try {
			Product product=service.findProductByPid(pid);
			request.setAttribute("product",product);
			String pids=pid;
			Cookie[] cookies=request.getCookies();
			if(cookies!=null) {
				for(Cookie cookie:cookies) {
					if("pids".equals(cookie.getName())) {
						pids=cookie.getValue();
						//1-3-2 本次访问2 --> 2-1-3
						String[] spilt=pids.split("-");
						LinkedList<String> list=new LinkedList<String>(Arrays.asList(spilt));
						//判断集合中是否存在当前pid
						if(list.contains(pid)) {
							list.remove(pid);
						}
						list.addFirst(pid);
						//将集合中数据转为字符串
						StringBuffer sb=new StringBuffer();
						for(int i=0;i<list.size()&&i<7;i++) {
							sb.append(list.get(i)+"-");
						}
						pids=sb.substring(0, sb.length()-1);
						
						
						
					}
				}
			}
			
			Cookie new_cookie=new Cookie("pids", pids);
			response.addCookie(new_cookie);
			
			request.getRequestDispatcher("/product_info.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println("查询商品出错！");
			response.setHeader("refresh","2,url=/shop/index.jsp");
		}
		out.flush();
		out.close();
	}
	
	
	//添加商品到购物车
	public void addProductToCart(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		
		//获取放入购物车商品pid
		String pid=request.getParameter("pid");
		
		//获取购买数量
		int buyNum=Integer.parseInt(request.getParameter("buyNum"));
		
		//获得product
		ProductService service=new ProductService();
		Product product=null;
		try {
			product=service.findProductByPid(pid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println("查询商品出错！");
			response.setHeader("refresh","2,url=/shop/index.jsp");
		}
		//计算小计
		double subtotal=product.getShop_price()*buyNum;
		//封装CartItem
		CartItem item=new CartItem();
		item.setProduct(product);
		item.setBuyNum(buyNum);
		item.setSubtotal(subtotal);
		
		//获得购物车  --  判断是否在session存在购物车
		Cart cart=(Cart) request.getSession().getAttribute("cart");
		if(cart==null) {
			cart=new Cart();
		}
		
		//将购物项放入购物车
		//判断购物车是否已存在此购物项
		Map<String,CartItem> cartIetms=cart.getCartItems();
		
		if(cartIetms.containsKey(pid)) {
			CartItem cartItem=cartIetms.get(pid);
			int oldBuyNum=cartItem.getBuyNum();
			oldBuyNum+=buyNum;
			cartItem.setBuyNum(oldBuyNum);
			subtotal+=cartItem.getSubtotal();
			cartItem.setSubtotal(subtotal);
		}else {
			cart.getCartItems().put(product.getPid(), item);
		}
		
		double total=cart.getTotal()+product.getShop_price()*buyNum;
		cart.setTotal(total);
		
		
		request.getSession().setAttribute("cart", cart);
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
		
		out.flush();
		out.close();
	}
	
	//删除单一商品
	public void delProFromCart(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		
		//获取要删除的pid
		String pid=request.getParameter("pid");
		Cart cart=(Cart) request.getSession().getAttribute("cart");
		if(cart!=null) {
			Map<String, CartItem> cartItems=cart.getCartItems();
			//修改总价
			cart.setTotal(cart.getTotal()-cartItems.get(pid).getSubtotal());
			//删除
			cartItems.remove(pid);
		}
		request.getSession().setAttribute("cart", cart);
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
		
		out.flush();
		out.close();
	}
	
	//清空购物车
	public void clearCart(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
		request.getSession().removeAttribute("cart");
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
		
	}
	
	//提交订单
	public void submitOrder(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		
		User user=(User) request.getSession().getAttribute("user");
//		if(user==null) {
//			response.sendRedirect(request.getContextPath()+"/login.jsp");
//		}
		
		Order order=new Order();
		//订单号
		String oid=CommonUtils.getUUID();
		order.setOid(oid);
		//下单时间
		order.setOrdertime(new Date());
		//总计
		Cart cart=(Cart) request.getSession().getAttribute("cart");
		double total=cart.getTotal();
		order.setTotal(total);
		//支付状态
		order.setState(0);
		//下单人
		order.setUser(user);
		
		//订单中的订单项
		Map<String, CartItem> cartItems=cart.getCartItems();
		for(Map.Entry<String,CartItem> entry:cartItems.entrySet()) {
			CartItem cartItem=entry.getValue();
			OrderItem orderItem=new OrderItem();
			orderItem.setItemid(CommonUtils.getUUID());
			orderItem.setCount(cartItem.getBuyNum());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setOrder(order);
			
			order.getOrderItems().add(orderItem);
		}
		
		ProductService service=new ProductService();
		try {
			service.submitOrder(order);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println("提交出错！");
			response.setHeader("refresh","2,url=/shop/index.jsp");
		}
		
		request.getSession().setAttribute("order", order);
		response.sendRedirect(request.getContextPath()+"/order_info.jsp");
		
		out.flush();
		out.close();
	}
	
	
	//确认订单
	public void confirmOrder(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		//更新收货人信息
		Map<String, String[]> properties=request.getParameterMap();
		Order order=new Order();
		try {
			BeanUtils.populate(order,properties);
			ProductService service=new ProductService();
			service.updateOrderAddress(order);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println("提交信息出错！");
			response.setHeader("refresh","2,url=/shop/cart.jsp");
		}
		
		//2、在线支付
				/*if(pd_FrpId.equals("ABC-NET-B2C")){
					//介入农行的接口
				}else if(pd_FrpId.equals("ICBC-NET-B2C")){
					//接入工行的接口
				}*/
				//.......

				//只接入一个接口，这个接口已经集成所有的银行接口了  ，这个接口是第三方支付平台提供的
				//接入的是易宝支付
				// 获得 支付必须基本数据
				String orderid = request.getParameter("oid");
				//String money = order.getTotal()+"";//支付金额
				String money = "0.01";//支付金额
				// 银行
				String pd_FrpId = request.getParameter("pd_FrpId");

				// 发给支付公司需要哪些数据
				String p0_Cmd = "Buy";
				String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
				String p2_Order = orderid;
				String p3_Amt = money;
				String p4_Cur = "CNY";
				String p5_Pid = "";
				String p6_Pcat = "";
				String p7_Pdesc = "";
				// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
				// 第三方支付可以访问网址
				String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
				String p9_SAF = "";
				String pa_MP = "";
				String pr_NeedResponse = "1";
				// 加密hmac 需要密钥
				String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
						"keyValue");
				String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
						p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
						pd_FrpId, pr_NeedResponse, keyValue);


				String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId="+pd_FrpId+
						"&p0_Cmd="+p0_Cmd+
						"&p1_MerId="+p1_MerId+
						"&p2_Order="+p2_Order+
						"&p3_Amt="+p3_Amt+
						"&p4_Cur="+p4_Cur+
						"&p5_Pid="+p5_Pid+
						"&p6_Pcat="+p6_Pcat+
						"&p7_Pdesc="+p7_Pdesc+
						"&p8_Url="+p8_Url+
						"&p9_SAF="+p9_SAF+
						"&pa_MP="+pa_MP+
						"&pr_NeedResponse="+pr_NeedResponse+
						"&hmac="+hmac;

				//重定向到第三方支付平台
				response.sendRedirect(url);
		
		
		
		out.flush();
		out.close();
		
	}
	
	public void myOrder(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		HttpSession session = request.getSession();

		User user = (User) session.getAttribute("user");
		
		ProductService service = new ProductService();
		//查询该用户的所有的订单信息(单表查询orders表)
		//集合中的每一个Order对象的数据是不完整的 缺少List<OrderItem> orderItems数据
		List<Order> orderList = service.findAllOrders(user.getUid());
		//循环所有的订单 为每个订单填充订单项集合信息
		if(orderList!=null){
			for(Order order : orderList){
				//获得每一个订单的oid
				String oid = order.getOid();
				//查询该订单的所有的订单项---mapList封装的是多个订单项和该订单项中的商品的信息
				List<Map<String, Object>> mapList = service.findAllOrderItemByOid(oid);
				//将mapList转换成List<OrderItem> orderItems 
				for(Map<String,Object> map : mapList){
					
					try {
						//从map中取出count subtotal 封装到OrderItem中
						OrderItem item = new OrderItem();
						//item.setCount(Integer.parseInt(map.get("count").toString()));
						BeanUtils.populate(item, map);
						//从map中取出pimage pname shop_price 封装到Product中
						Product product = new Product();
						BeanUtils.populate(product, map);
						//将product封装到OrderItem
						item.setProduct(product);
						//将orderitem封装到order中的orderItemList中
						order.getOrderItems().add(item);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
					
					
				}

			}
		}
		
		
		//orderList封装完整了
		request.setAttribute("orderList", orderList);
		
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
		
	}
	
	
	

}
