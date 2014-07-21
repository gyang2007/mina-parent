package com.gy.mina.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解方法，表明方法映射为Http处理RUL
 * @author gyang
 *
 */

@Target(value={ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpMapping {
	public String value();	// 映射URL地址
	public Type type() default Type.TEXT;	// 返回结果类型
	
	/**
	 * 结果类型
	 * 
	 * @author gyang
	 *
	 */
	enum Type {
		TEXT("Text"),	// 文本
		JSON("Json"),	// json字符串
		OBJECT("Object");	// Java对象
		
		private String value;
		private Type(String value) {
			this.value = value;
		}
		
		public String value() {
			return this.value;
		}
	}
}
