package com.gy.mina.http.core.util;

import java.io.Serializable;

public class Util {

	/**
	 * 校验对象是否可序列化
	 * 
	 * @param responseMsg
	 * @return
	 */
	public static boolean checkMsg(Object responseMsg) {
		Class<?>[] cc = responseMsg.getClass().getInterfaces();
		for(Class<?> c : cc) {
			if(c == Serializable.class) {
				return true;
			}
		}
		
		return false;
	}
}
