package com.gy.mina.http.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Mina Server工程入口函数
 * @author gyang
 *
 */
public class HttpServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String config = "spring/applicationContext.xml";
		new ClassPathXmlApplicationContext(config);
		// 检查服务器环境设置
		// ...
	}

}
