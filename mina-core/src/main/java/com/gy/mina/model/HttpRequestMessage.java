package com.gy.mina.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.mina.http.HttpRequestImpl;
import org.apache.mina.http.api.HttpMethod;
import org.apache.mina.http.api.HttpVersion;

import com.gy.mina.enums.HttpHeaders;

public class HttpRequestMessage extends HttpRequestImpl {
	protected final HttpVersion version;

    protected final HttpMethod method;

    protected final String requestedPath;
    
    protected final String queryString;

    protected final Map<String, String> headers;

	/**
	 * POST方法的消息体对象，必须能够序列化
	 */
	protected Object body;

	public HttpRequestMessage(HttpVersion version, HttpMethod method,
			String requestedPath, String queryString,
			Map<String, String> headers) {
		super(version, method, requestedPath, queryString, headers);
		
        this.version = version;
        this.method = method;
        this.requestedPath = requestedPath;
        this.queryString = queryString;
        this.headers = headers;
	}
	
	@Override
	public boolean isKeepAlive() {
		return (HttpHeaders.Connection.KEEP_ALIVE.toString().equals(getHeader(HttpHeaders.Key.CONNECTION.value()))) ? true : false;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	@Override
	public String toString() {
        String result = "HTTP REQUEST METHOD: " + method + "\n";
        result += "VERSION: " + this.version + "\n";
        result += "PATH: " + requestedPath + "\n";
        result += "QUERY:" + queryString + "\n";

        result += "--- HEADER --- \n";
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            result += key + ":" + value + "\n";
        }

        if(queryString != null && !queryString.trim().equals("")) {
        	result += "--- PARAMETERS --- \n";
        	Map<String, List<String>> parameters = getParameters();
        	for (String key : parameters.keySet()) {
        		Collection<String> values = parameters.get(key);
        		for (String value : values) { result += key + ":" + value + "\n"; }
        	}
        }
        
		return result;
//		return result + (this.body != null ? ("\nbody:" + this.body.toString()) : "");
	}
}
