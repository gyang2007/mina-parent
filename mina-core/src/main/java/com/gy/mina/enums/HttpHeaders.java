package com.gy.mina.enums;

/**
 * 定义HTTP请求HEADERS键值对
 * 
 * @author gyang
 *
 */
public interface HttpHeaders {
	/**
	 * Http Header头的Key值
	 * 
	 * @author gyang
	 *
	 */
	public enum Key {
		CACHE_CONTROL("Cache-Control"),
		CONNECTION("Connection"),
		CONTENT_TYPE("Content-Type"),
		CONTENT_LENGTH("Content-Length"),
		INNER_TYPE("Inner-Type")	// 自定义Header值，标识Body数据类型
		;
		
		private String value;
		private Key(String value) {
			this.value = value;
		}
		
		public String value() {
			return this.value;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	/**
	 * Cache-Control
	 * 
	 * @author gyang
	 *
	 */
	public enum CacheControl {
		NO_CACHE("no-cache"),
		PRIVATE("private")
		;
		
		private String value;
		private CacheControl(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	/**
	 * CONNECTION
	 * 
	 * @author gyang
	 *
	 */
	public enum Connection {
		CLOSE("close"),
		KEEP_ALIVE("keep-alive")
		;
		
		private String value;
		private Connection(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	/**
	 * CONTENT-TYPE
	 * 
	 * @author gyang
	 *
	 */
	public enum ContentType{
		TEXT_HTML("text/html"),
		TEXT_PLAIN("text/plain"),
		TEXT_JSON("text/json")
		;
		
		private static final String CHARSET_DEFAULT = "UTF-8";
		private String value;
		private ContentType(String value) {
			this.value = value;
		}
		
		public String value() {
			return this.value("UTF-8");
		}
		
		public String value(String charsetName) {
			return this.value + "; " + (((charsetName == null) || "".equals((charsetName.trim()))) ? CHARSET_DEFAULT : charsetName);
		}
		
		@Override
		public String toString() {
			return this.value();
		}
	}
}
