package com.gy.mina.http.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 资源文件加载管理类
 * 
 * @author gyang
 *
 */
public class ResourceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);
	
	private static final ResourceManager INSTANCE = new ResourceManager();
	
	private static URLMappingResourceManager urlMappingResourceManager;
	private static FilterResourceManager filterResourceManager;
	
	/**
	 * 默认配置文件
	 */
	private static final String CONFIG_DEFAULT = "/config.xml";
	
	private static final String ELEMENT_FILTERS = "filters";
	private static final String ELEMENT_URLS = "urls";
	
	static{
		InputStream inputStream = null;
		try{
			inputStream = URLMappingResourceManager.class.getResourceAsStream(CONFIG_DEFAULT);
			if(inputStream == null) {
				LOGGER.error("File config.xml is not found...");
				
				System.exit(-1);
			}
			
			LOGGER.info("Load config.xml...");
			SAXReader saxReader = new SAXReader();
			try {
				Document rootDoc = saxReader.read(inputStream);
				Element rootElement = rootDoc.getRootElement();
				Iterator<Element> iterator = rootElement.elementIterator();
				Element nextElement;
				while(iterator.hasNext()) {
					nextElement = iterator.next();
					String elementName = nextElement.getName();
					// 过滤器解析
					if(ELEMENT_FILTERS.equals(elementName)) {
						filterResourceManager = new FilterResourceManager(nextElement);
					}
					// URL地址解析
					else if(ELEMENT_URLS.equals(elementName)) {
						urlMappingResourceManager = new URLMappingResourceManager(nextElement);
					}
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private ResourceManager() {
		
	}
	
	public static ResourceManager getInstance() {
		return INSTANCE;
	}
	
	public URLMappingResourceManager getURLMappingResourceManager() {
		return urlMappingResourceManager;
	}
	
	public FilterResourceManager getFilterResourceManager() {
		return filterResourceManager;
	}
}
