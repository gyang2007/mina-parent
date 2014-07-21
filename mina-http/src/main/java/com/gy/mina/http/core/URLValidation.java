package com.gy.mina.http.core;

/**
 * 校验URL映射地址
 * 
 * @author gyang
 *
 */
public class URLValidation {
	/**
	 * URL地址后缀名 
	 */
	public static final String HTM_POSTFIX = ".htm";
	
	/**
	 * 简单校验URL地址<br />
	 * URL地址规则：<br />
	 * 1、以“/”开头<br />
	 * 2、以“.htm”后缀名结尾(自动添加.htm后缀名)
	 * 
	 * @param url
	 * @return
	 */
	public static String filterUrlPath(String url) {
		String newUrl = url;
		if(!newUrl.startsWith("/")) {
			newUrl = "/" + newUrl;
		}
		
		if(!newUrl.endsWith(".htm")) {
			newUrl += HTM_POSTFIX;
		}
		
		return newUrl;
	}
}
