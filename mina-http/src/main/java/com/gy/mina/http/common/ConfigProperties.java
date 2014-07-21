package com.gy.mina.http.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigProperties {
	private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("mina.exception");
	
	private static final String CONFIG_FILE = "/config.properties";
	
	public static final String PROJECT_HOME;
	
	private ConfigProperties() {
		
	}
	
	static{
		InputStream in = null;
		try {
			in = ConfigProperties.class.getResourceAsStream(CONFIG_FILE);
			
			Properties configProperties = new Properties();
			configProperties.load(in);
			
			PROJECT_HOME = configProperties.getProperty("project.home");
			
		} catch (IOException e) {
			ERROR_LOGGER.error("加载资源文件失败...", e);
			throw new RuntimeException("加载资源文件失败");
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
