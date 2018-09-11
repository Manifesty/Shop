package com.dh.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rmi.CORBA.Tie;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.ls.LSInput;

import com.dh.model.Category;
import com.dh.model.Order;
import com.dh.model.Product;
import com.dh.service.AdminService;
import com.dh.utils.CommonUtils;

import net.sf.json.JSONArray;

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet(
		urlPatterns= {
				"/admin/findAllCategory.do","/admin/addProduct.do",
				"/admin/findAllOrder.do","/admin/findOrderInfoByOid.do"
		}
)
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminServlet() {
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
			response.setHeader("refresh", "2;url=/shop/admin/index.jsp");
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
	
	//ajax传商品分类到前端
	public void findAllCategory(HttpServletRequest request,HttpServletResponse response) throws IOException {
		//提供一个List<Category>转成json串
		PrintWriter out=response.getWriter();
		response.setContentType("text/html;charset=utf-8");
		AdminService service =new AdminService();
		List<Category> categoryList= service.findAllCategory();
		JSONArray json=JSONArray.fromObject(categoryList);
		out.println(json.toString());
		
		out.flush();
		out.close();
		
	}
	
	public void addProduct(HttpServletRequest request,HttpServletResponse response) throws IOException {
		//目的：收集表单的数据 封装一个Product实体 将上传图片存到服务器磁盘上
		PrintWriter out=response.getWriter();
		response.setContentType("text/html;charset=utf-8");
		
		Product product=new Product();
		
		//收集数据的容器
		Map<String,Object> map=new HashMap<String,Object>();
		
		try {
			//创建磁盘文件项工厂
			DiskFileItemFactory factory=new DiskFileItemFactory();
			//创建文件上传核心对象
			ServletFileUpload upload=new ServletFileUpload(factory);
			//解析request获得文件项对象集合
			List<FileItem> parseRequest=upload.parseRequest(request);
			for(FileItem item:parseRequest) {
				//判断是否为普通表单项
				boolean formField=item.isFormField();
				if(formField) {
					//普通表单项 获得表单数据封装到Product实体
					String fieldName=item.getFieldName();
					String fieldValue=item.getString("UTF-8");
					map.put(fieldName, fieldValue);
				}else {
					//文件上传项 获得文件名称 获得文件内容
					String fileName=item.getName();
					String path=this.getServletContext().getRealPath("upload");
					InputStream in =item.getInputStream();
					OutputStream output =new FileOutputStream(path+"/"+fileName);
					IOUtils.copy(in, output);
					in.close();
					output.close();
					item.delete();
					
					map.put("pimage","upload/"+fileName);
				}
			}
			
			BeanUtils.populate(product, map);
			//是否对product数据封装完全
			product.setPid(CommonUtils.getUUID());
			product.setPdate(new Date());
			product.setPflag(0);
			Category category=new Category();
			category.setCid(map.get("cid").toString());
			
			AdminService service=new AdminService();
			service.addProduct(product);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		out.flush();
		out.close();
		
	}
	
	//获得所有订单
	public void findAllOrder(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		//提供一个List<Category>转成json串
		PrintWriter out=response.getWriter();
		response.setContentType("text/html;charset=utf-8");
		AdminService service =new AdminService();
		List<Order> orderList= service.findAllOrder();
		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
		
		out.flush();
		out.close();
		
	}
	
	//根据oid查询订单信息和商品信息
	public void findOrderInfoByOid(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out=response.getWriter();
		response.setContentType("text/html;charset=utf-8");
		
		String oid =request.getParameter("oid");
		AdminService service=new AdminService();
		List<Map<String, Object>> mapList=service.findOrderInfoByOid(oid);
		JSONArray json=JSONArray.fromObject(mapList);
		out.println(json.toString());
		
		out.flush();
		out.close();
	}
	

}
