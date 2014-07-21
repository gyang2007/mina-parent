package com.gy.mina.model;

import java.io.Serializable;
import java.util.Map;

public class BaseParamModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 交互通信参数
	 */
	private Map<String, Object> paramMap;
	
	public BaseParamModel() {
	
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
	
}
