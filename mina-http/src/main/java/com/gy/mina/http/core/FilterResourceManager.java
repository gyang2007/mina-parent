package com.gy.mina.http.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解析用户自定义过滤器
 * 
 * @author gyang
 *
 */
public class FilterResourceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterResourceManager.class);
	
	/**
	 * 用户自定义过滤器集合
	 */
	private List<Class<?>> filters = new ArrayList<Class<?>>();
	
	public FilterResourceManager(Element element) {
		this.parseFilters(element);
	}
	
	/**
	 * 用户自定义过滤器集合
	 * 
	 * @return
	 */
	public List<Class<?>> getFilterClasses() {
		return filters;
	}
	
	private void parseFilters(Element element) {
		List<Element> elements = element.elements("filter");
		if(elements == null || elements.size() == 0) {
			LOGGER.warn("There is no filter elements...");
			
			return;
		}
		
		Set<Class<?>> clazzSet = new LinkedHashSet<Class<?>>();
		for(Element mapping : elements) {
			Element nameElement = mapping.element("name");
			if(nameElement == null) {
				throw new IllegalArgumentException("There is no 'name' element...");
			}
			else {
				String className = nameElement.getTextTrim();
				if(className == null || "".equals(className)) {
					throw new IllegalArgumentException("Class name is invalid...");
				}
				
				try {
					clazzSet.add(Class.forName(className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		
		this.filters.addAll(clazzSet);
		LOGGER.info("Parsed filters size " + filters.size() + "...");
	}
	
}
