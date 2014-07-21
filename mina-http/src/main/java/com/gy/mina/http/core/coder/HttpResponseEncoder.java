package com.gy.mina.http.core.coder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gy.mina.anno.HttpMapping;
import com.gy.mina.enums.HttpHeaders;
import com.gy.mina.model.HttpResponseMessage;

public class HttpResponseEncoder implements MessageEncoder<HttpResponseMessage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseEncoder.class);

	private static final byte[] CRLF = new byte[] { '\r', '\n' };
	private static final String KEY_VALUE_SEPERATOR = ": ";
	
	public HttpResponseEncoder() {
		
	}
	
	@Override
	public void encode(IoSession session, HttpResponseMessage message,
			ProtocolEncoderOutput out) throws Exception {
		LOGGER.info("Http response encode:\n" + message.toString());
		IoBuffer buf = IoBuffer.allocate(256);
		// Enable auto-expand for easier encoding
		buf.setAutoExpand(true);

		try {
			// output all headers except the content length
			CharsetEncoder encoder = Charset.defaultCharset().newEncoder();
			buf.putString(message.getStatus().line(), encoder);
			for (Entry<String, String> entry : message.getHeaders().entrySet()) {
				buf.putString(entry.getKey(), encoder);
				buf.putString(KEY_VALUE_SEPERATOR, encoder);
				buf.putString(entry.getValue(), encoder);
				buf.put(CRLF);
			}
			// 传输对象的数据类型，Java Http客户端根据数据类型解析Body
			buf.putString(HttpHeaders.Key.INNER_TYPE.value() + KEY_VALUE_SEPERATOR + message.getType().value(), encoder);
			buf.put(CRLF);
			// now the content length is the body length
			Object body = message.getBody();
			if(body != null) {
				buf.putString(HttpHeaders.Key.CONTENT_LENGTH.value() + KEY_VALUE_SEPERATOR, encoder);
				buf.putString(String.valueOf(getBodyLength(message.getBody(), message.getType())), encoder);
			}
			
			buf.put(CRLF);
			buf.put(CRLF);
			if(body != null) {
				switch (message.getType()) {
				case TEXT:
				case JSON:
					buf.put(body.toString().getBytes(Charset.defaultCharset().name()));
					break;
					
				case OBJECT:
					buf.putObject(message.getBody());
					break;
				}
/*				if(body instanceof String) {
					String tmp = (String) body;
					buf.put(tmp.getBytes(Charset.defaultCharset().name()));
				}
				else {
					buf.putObject(message.getBody());
				}*/
			}
		} catch (CharacterCodingException ex) {
			ex.printStackTrace();
		}

		buf.flip();
		out.write(buf);
	}

	/**
	 * 获取BODY对象序列化后的长度
	 * 
	 * @param o
	 * @return
	 */
	private int getBodyLength(Object o, HttpMapping.Type type) {
		switch (type) {
		case TEXT:
		case JSON:
			String tmp = o.toString();
			try {
				return tmp.getBytes(Charset.defaultCharset().name()).length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		case OBJECT:
			IoBuffer ioBuffer = IoBuffer.allocate(256);
			ioBuffer.setAutoExpand(true);
			ioBuffer.putObject(o);
			return ioBuffer.position();
			
		default:
			return 0;
		}
	}
	
}
