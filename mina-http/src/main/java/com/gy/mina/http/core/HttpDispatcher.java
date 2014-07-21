package com.gy.mina.http.core;

import java.io.NotSerializableException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.HttpStatus;
import org.apache.mina.http.api.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gy.mina.anno.HttpMapping;
import com.gy.mina.enums.HttpHeaders;
import com.gy.mina.http.core.util.Util;
import com.gy.mina.model.HttpRequestMessage;
import com.gy.mina.model.HttpResponseMessage;


/**
 * Http请求统一转发
 * 
 * @author gyang
 *
 */
public class HttpDispatcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpDispatcher.class);
	
	private URLMappingResourceManager mappingResourceManager;
	
	public HttpDispatcher() {
		mappingResourceManager = ResourceManager.getInstance().getURLMappingResourceManager();
	}
	
	/**
	 * 解析请求地址，映射到具体的方法调用
	 * 
	 * @param session 
	 * @param message 客户端请求参数对象
	 */
	public void dispatch(IoSession session, Object message) {
		if(message instanceof HttpRequestMessage) {
			HttpRequestMessage requestMessage = (HttpRequestMessage) message;
			String url = URLValidation.filterUrlPath(requestMessage.getRequestPath());
			LOGGER.info("Http request url " + url + "...");
			Method method = mappingResourceManager.getMethod(url);
			if(method == null) {
				responseNotFound(session, message);
				return;
			}
			
			Exception ex = null;
			try {
				Class<?> c = method.getDeclaringClass();
				Object o = method.invoke(c.newInstance(), session, message);
				// 如果方法调用有结果，返回到客户端
				if(o != null) {
					HttpMapping mappingAnno = method.getAnnotation(HttpMapping.class);
					response(session, o, mappingAnno.type());
				}
			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
				ex = e;
			} catch (IllegalAccessException e) {
//				e.printStackTrace();
				ex = e;
			} catch (InvocationTargetException e) {
//				e.printStackTrace();
				ex = e;
			} catch (InstantiationException e) {
//				e.printStackTrace();
				ex = e;
			}
			
			if(ex != null) {
				responseError(session, message);
				LOGGER.error("Http request error...\n", ex);
			}
			return;
		}
		
		responseNotFound(session, message);
	}
	
	/**
	 * 请求地址未找到
	 * 
	 * @param session
	 * @param message
	 */
	private void responseNotFound(IoSession session, Object message) {
		HttpResponseMessage responseMessage = new HttpResponseMessage(HttpVersion.HTTP_1_1, HttpStatus.CLIENT_ERROR_NOT_FOUND, new HashMap<String, String>());
		session.write(responseMessage).addListener(IoFutureListener.CLOSE);
		LOGGER.error("Http request not found url...");
	}
	
	/**
	 * 服务器内部错误
	 * 
	 * @param session
	 * @param message
	 */
	private void responseError(IoSession session, Object message) {
		HttpResponseMessage responseMessage = new HttpResponseMessage(HttpVersion.HTTP_1_1, HttpStatus.SERVER_ERROR_INTERNAL_SERVER_ERROR, new HashMap<String, String>());
		session.write(responseMessage).addListener(IoFutureListener.CLOSE);
	}
	
	/**
	 * 将Controller调用结果返回客户端
	 * 
	 * @param session
	 * @param responseMsg
	 */
	private void response(IoSession session, Object responseMsg, HttpMapping.Type type) {
		if(HttpMapping.Type.OBJECT == type && ! Util.checkMsg(responseMsg)) {
			responseError(session, responseMsg);
			LOGGER.error(new NotSerializableException(responseMsg.getClass().getName()).getLocalizedMessage());
		}

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpHeaders.Key.CONNECTION.value(), HttpHeaders.Connection.CLOSE.toString());
		HttpResponseMessage httpResponse = new HttpResponseMessage(HttpVersion.HTTP_1_1, HttpStatus.SUCCESS_OK, headers);
		httpResponse.setBody(responseMsg);
		httpResponse.setType(type);
		session.write(httpResponse);
	}
}
