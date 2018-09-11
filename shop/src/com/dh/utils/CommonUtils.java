package com.dh.utils;

import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;

public class CommonUtils {
		
		/**
		 * @Title: getUUID 
		 * @Description: TODO(获取uuid) 
		 * @param @return  参数说明 
		 * @return String    返回类型 
		 * @throws 
		 */
		public static String getUUID() {
			return UUID.randomUUID().toString();
		}
		
		/**
		 * @throws GeneralSecurityException 
		 * @Title: sendMail 
		 * @Description: TODO(发送邮件) 
		 * @param @param email
		 * @param @param emailMsg  参数说明 
		 * @return void    返回类型 
		 * @throws 
		 */
		public static void sendMail(String email, String emailMsg)
				throws AddressException, MessagingException {
			// 1.创建一个程序与邮件服务器会话对象 Session

			Properties props = new Properties();
			props.setProperty("mail.debug", "true");
			props.setProperty("mail.transport.protocol", "SMTP");//设置传输协议
			props.setProperty("mail.host", "smtp.126.com");//设置发件人的stmp服务器地址
			props.setProperty("mail.smtp.auth", "true");//设置用户认证方式 指定验证为true
			
			//开启ssl加密
			MailSSLSocketFactory sf=null;
			try {
				sf = new MailSSLSocketFactory();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sf.setTrustAllHosts(true);
			props.setProperty("mail.smtp.ssl.enable", "true");
			props.setProperty("mail.smtp.ssl.socketFactory", "true");

			// 创建验证器
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("bean_D@126.com", "dh717829");
				}
			};

			Session session = Session.getInstance(props, auth);

			// 2.创建一个Message，它相当于是邮件内容
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress("bean_D@126.com")); // 设置发送者

			message.setRecipient(RecipientType.TO, new InternetAddress(email)); // 设置发送方式与接收者

			message.setSubject("用户激活");
			// message.setText("这是一封激活邮件，请<a href='#'>点击</a>");

			message.setContent(emailMsg, "text/html;charset=utf-8");

			// 3.创建 Transport用于将邮件发送

			Transport.send(message);
		}
		
		
}
