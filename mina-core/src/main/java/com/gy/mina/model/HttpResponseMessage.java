package com.gy.mina.model;

import java.util.Map;

import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;
import org.apache.mina.http.api.HttpVersion;

import com.gy.mina.anno.HttpMapping;
import com.gy.mina.enums.HttpHeaders;


public class HttpResponseMessage implements HttpResponse {
	
    private final HttpVersion version;

    private final HttpStatus status;

    private final Map<String, String> headers;

    /** Storage for body of HTTP response. */
//    private final ByteArrayOutputStream body = new ByteArrayOutputStream(1024);
    private Object body;
    
    private HttpMapping.Type type = HttpMapping.Type.TEXT;
    
    public HttpResponseMessage(HttpVersion version, HttpStatus status, Map<String, String> headers) {
        this.version = version;
        this.status = status;
        this.headers = headers;
	}

    @Override
	public HttpVersion getProtocolVersion() {
		return version;
	}

    @Override
	public String getContentType() {
		return headers.get(HttpHeaders.Key.CONTENT_TYPE.value());
	}
	
	private void setContentType(String type) {
		headers.put(HttpHeaders.Key.CONTENT_TYPE.value(), type);
	}

	@Override
	public boolean isKeepAlive() {
		return (HttpHeaders.Connection.KEEP_ALIVE.toString().equals(getHeader(HttpHeaders.Key.CONNECTION.value()))) ? true : false;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}
	
	public void putHeader(String key, String value) {
		this.headers.put(key, value);
	}

    public void setBody(Object body) {
        this.body = body;
    }

    public Object getBody() {
        return this.body;
    }
    
    public HttpMapping.Type getType() {
		return type;
	}

	public void setType(HttpMapping.Type type) {
		this.type = type;
		switch (type) {
		case TEXT:
			this.setContentType(HttpHeaders.ContentType.TEXT_PLAIN.value());
			break;
		case JSON:
			this.setContentType(HttpHeaders.ContentType.TEXT_JSON.value());
			break;

		case OBJECT:
			this.setContentType(HttpHeaders.ContentType.TEXT_HTML.value());
			break;
		}
	}

	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder(128);
    	sb.append("VERSION: ").append(version).append("\n")
    	.append("STATUS: ").append(status).append("\n")
    	.append("------------ HEADERS ------------").append("\n");
    	for(String key : headers.keySet()) {
    		String value = headers.get(key);
    		sb.append(key).append(": ").append(value).append("\n");
    	}
/*    	if(body != null) {
    		sb.append("----------- BODY -----------").append("\n");
    		sb.append("BODY: ").append(body.toString());
    	}*/
    	
    	return sb.toString();
    }
}
