package com.gy.mina.http.core;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gy.mina.anno.HttpController;
import com.gy.mina.anno.HttpMapping;


/**
 * 解析HTTP地址
 * 
 * @author gyang
 *
 */
public class URLMappingParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(URLMappingParser.class);
	
	private Class<?>[] classes = null;
	
	public URLMappingParser(Class<?>[] classes) {
		this.classes = classes;
	}
	
	/**
	 * 加载URL地址映射类文件，解析URL地址映射
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public Map<String, Method> getURLMapping() throws MalformedURLException {
		Map<String, Method> urlMapping = new HashMap<String, Method>();
		
		for(Class<?> c : classes) {
			HttpController httpController = c.getAnnotation(HttpController.class);
			if(httpController == null) {
				LOGGER.info(c.getName() + " is not a controller...");
			}
			else {
				Method[] methods = c.getMethods();
				for(Method method : methods) {
					HttpMapping httpMapping = method.getAnnotation(HttpMapping.class);
					if(httpMapping != null) {
						String urlStr = httpMapping.value();
						
						String newUrl = URLValidation.filterUrlPath(urlStr);
						if(urlMapping.containsKey(newUrl)) {
							throw new MalformedURLException("Duplicate url mapping " + newUrl + " ...");
						}
						urlMapping.put(newUrl, method);
						LOGGER.info("Add url mapping: " + newUrl + " ---> " + method.getName() + "...");
					}
				}
			}
		}
		
		return urlMapping;
	}
}
