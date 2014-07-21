package com.gy.mina.http.core;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一管理URL、方法映射
 * @author gyang
 *
 */
public class URLMappingResourceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(URLMappingResourceManager.class);
	
	/**
	 * URL地址映射
	 */
	private Map<String, Method> urlMapping = new HashMap<String, Method>();
	
	public URLMappingResourceManager(Element element) {
		this.parseURLMappings(element);
	}
	
	public Map<String, Method> getUrlMapping() {
		return urlMapping;
	}
	
	public Method getMethod(String url) {
		return urlMapping.get(URLValidation.filterUrlPath(url));
	}
	
	private void parseURLMappings(Element e) {
		List<Element> elements = e.elements("mapping");
		if(elements == null || elements.size() == 0) {
			LOGGER.warn("There is no url mapping...");
			
			return;
		}
		
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for(Element mapping : elements) {
			Element packageE = mapping.element("package");
			if(packageE == null) {
				throw new IllegalArgumentException("There is no 'package' element matching mapping...");
			}
			else {
				String packageName = packageE.getTextTrim();
				if(packageName == null || "".equals(packageName)) {
					throw new IllegalArgumentException("Package name is invalid...");
				}
				
				XMLParserCommon xmlParserCommon = new XMLParserCommon();
				classes.addAll(xmlParserCommon.getClasses(packageName));
			}
		}
		
		Class<?>[] cls = new Class<?>[classes.size()];
		URLMappingParser parser = new URLMappingParser(classes.toArray(cls));
		
		try {
			urlMapping.putAll(parser.getURLMapping());
		} catch (MalformedURLException ex) {
			LOGGER.error("Parse url mapping exception: " + ex.getLocalizedMessage());
//			ex.printStackTrace();
			
			// 映射地址解析错误，致命的异常，终止JVM，需要用户修复此异常
			System.exit(-1);
		}
		
		LOGGER.info("Parsed url mapping size " + urlMapping.size() + "...");
	}
	
}
