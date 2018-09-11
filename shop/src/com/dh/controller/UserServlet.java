package com.dh.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import com.dh.model.User;
import com.dh.service.UserService;
import com.dh.utils.CommonUtils;


/**
 * Servlet implementation class UserServlet
 */
@WebServlet(
		urlPatterns= {"/user/login.do","/user/register.do",
				"/user/active.do","/user/checkUsername.do",
				"/user/logout.do"
		},
		loadOnStartup=1
		)
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void register(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		request.setCharacterEncoding("utf-8");
		//获取表单数据
		Map<String , String[]> properties=request.getParameterMap();
		User user=new User();
		try {
			ConvertUtils.register(new Converter() {
				//String->Date
				@Override
				public Object convert(Class clazz, Object value) {
					// TODO Auto-generated method stub
					SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
					Date parse=null;
					try {
						parse=df.parse(value.toString());
					}catch(ParseException e) {
						e.printStackTrace();
					}
					return null;
				}
			},Date.class);
			//映射封装
			BeanUtils.populate(user, properties);
		}catch(IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		user.setUid(CommonUtils.getUUID());
		user.setTelephone(null);
		user.setState(0);
		String activeCode=CommonUtils.getUUID();
		user.setCode(activeCode);
		
		UserService service=new UserService();
		boolean isRegiseterSuccess=service.regist(user);
		if(isRegiseterSuccess) {
			String emailMsg = "恭喜您注册成功，请点击下面的连接进行激活账户"
					+ "<a href='http://localhost:8080/shop/user/active.do?activeCode="+activeCode+"'>"
							+ "http://localhost:8080/shop/user/active.do?activeCode="+activeCode+"</a>";
			try {
				CommonUtils.sendMail(user.getEmail(), emailMsg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
			
			//跳转到注册成功页面
			response.sendRedirect(request.getContextPath()+"/registerSuccess.jsp");
		}else{
			//跳转到失败的提示页面
			response.sendRedirect(request.getContextPath()+"/registerFail.jsp");
		}
		
		
	}
	
	public void active(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
		String activeCode=request.getParameter("activeCode");
		UserService service=new UserService();
		service.active(activeCode);
		response.sendRedirect(request.getContextPath()+"/login.jsp");
		
	}
	
	public void checkUsername(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		String username=request.getParameter("username");
		UserService service=new UserService();
		boolean isExist=service.checkUsername(username);
		String json="{\"isExist\":"+isExist+"}";
		response.getWriter().write(json);
		
		
	}
	
	//用户登录
		public void login(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			HttpSession session = request.getSession();

			//获得输入的用户名和密码
			String username = request.getParameter("username");
			String password = request.getParameter("password");

			//将用户名和密码传递给service层
			UserService service = new UserService();
			User user = null;
			try {
				user = service.login(username,password);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			//判断用户是否登录成功 user是否是null
			if(user!=null){
				//登录成功
				//***************判断用户是否勾选了自动登录*****************
				String autoLogin = request.getParameter("autoLogin");
				if("autoLogin".equals(autoLogin)){
					//要自动登录
					//创建存储用户名的cookie
					Cookie cookie_username = new Cookie("cookie_username",user.getUsername());
					cookie_username.setMaxAge(10*60);
					//创建存储密码的cookie
					Cookie cookie_password = new Cookie("cookie_password",user.getPassword());
					cookie_password.setMaxAge(10*60);

					response.addCookie(cookie_username);
					response.addCookie(cookie_password);

				}

				//***************************************************
				//将user对象存到session中
				session.setAttribute("user", user);

				//重定向到首页
				response.sendRedirect(request.getContextPath()+"/index.jsp");
			}else{
				request.setAttribute("loginError", "用户名或密码错误");
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}
		}
	
	
		//用户注销
		public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException{
			HttpSession session = request.getSession();
			//从session中将user删除
			session.removeAttribute("user");
			
			//将存储在客户端的cookie删除掉
			Cookie cookie_username = new Cookie("cookie_username","");
			cookie_username.setMaxAge(0);
			//创建存储密码的cookie
			Cookie cookie_password = new Cookie("cookie_password","");
			cookie_password.setMaxAge(0);

			response.addCookie(cookie_username);
			response.addCookie(cookie_password);
			
			
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			
		}	

}
