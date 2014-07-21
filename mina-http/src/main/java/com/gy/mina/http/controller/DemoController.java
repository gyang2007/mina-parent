package com.gy.mina.http.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.HttpStatus;
import org.apache.mina.http.api.HttpVersion;
import org.json.JSONObject;

import com.gy.mina.anno.HttpController;
import com.gy.mina.anno.HttpMapping;
import com.gy.mina.enums.HttpHeaders;
import com.gy.mina.model.HttpRequestMessage;
import com.gy.mina.model.HttpResponseMessage;

/**
 * 用户自定义Controller实例
 * <p>定义规则：</p>
 * 1、使用@HttpController注解标注类文件
 * 2、使用@HttpMapping注解标注映射的方法，value参数表明URL映射地址，type参数表明返回结果类型，方法声明必须为public
 * 3、如果方法内部已经向客户端输出结果(如调用IoSession对象的write方法等)，则方法返回空或NULL结果；否则方法返回结果本身(结果对象必须可序列化)，由该方法调用者负责向客户端输出结果
 * 
 * @author gyang
 *
 */

@HttpController
public class DemoController {

	@HttpMapping(value="/test/tc01.htm")
	public void tc01(IoSession session, Object message) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpHeaders.Key.CONNECTION.value(), HttpHeaders.Connection.CLOSE.toString());
		headers.put(HttpHeaders.Key.CONTENT_TYPE.value(), HttpHeaders.ContentType.TEXT_HTML.value());
		HttpResponseMessage httpResponse = new HttpResponseMessage(HttpVersion.HTTP_1_1, HttpStatus.SUCCESS_OK, headers);
		Map<String, String> bodyMap = new HashMap<String, String>();
		bodyMap.put("key1", "value1");
		bodyMap.put("key2", "value1");
		bodyMap.put("key3", "value1");
		bodyMap.put("key4", "value1");
		httpResponse.setBody(bodyMap);
		httpResponse.setType(HttpMapping.Type.OBJECT);	// 返回Java对象
		session.write(httpResponse);
	}
	
	@HttpMapping(value="/test/tc02.htm", type=HttpMapping.Type.TEXT)
	public String tc02(IoSession session, Object message) {
		return "返回结果示例";
	}
	
	@HttpMapping(value="/test/tc03.htm", type=HttpMapping.Type.JSON)
	public Object tc03(IoSession session, Object message) {
		Map<String, String> bodyMap = new HashMap<String, String>();
		bodyMap.put("key1", "value1");
		bodyMap.put("key2", "value1");
		bodyMap.put("key3", "value1");
		bodyMap.put("key4", "value1");
		
		return new JSONObject(bodyMap);
	}
	
	@HttpMapping(value="/test/tc04.htm", type=HttpMapping.Type.OBJECT)
	public Object tc04(IoSession session, Object message) {
		Map<String, String> bodyMap = new HashMap<String, String>();
		bodyMap.put("key1", "value1");
		bodyMap.put("key2", "value1");
		bodyMap.put("key3", "value1");
		bodyMap.put("key4", "value1");
		return bodyMap;
	}
	
	@HttpMapping(value="/test/transport.htm", type=HttpMapping.Type.OBJECT)
	public Object transport(IoSession session, Object message) {
		HttpRequestMessage msg = (HttpRequestMessage) message;
		String content = (String) msg.getBody();
		System.out.println("Length: " + content.length());
		
		return Boolean.TRUE;
	}
}
