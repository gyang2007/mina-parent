package com.gy.mina.http.core.coder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.apache.mina.http.ArrayUtil;
import org.apache.mina.http.api.HttpMethod;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gy.mina.enums.HttpHeaders;
import com.gy.mina.model.HttpRequestMessage;

public class HttpRequestDecoder extends MessageDecoderAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestDecoder.class);

	private static final byte[] CONTENT_LENGTH = new String(HttpHeaders.Key.CONTENT_LENGTH + ":")
			.getBytes();

	private final CharsetDecoder defaultDecoder = Charset.defaultCharset()
			.newDecoder();

	/** Regex to parse HttpRequest Request Line */
	public static final Pattern REQUEST_LINE_PATTERN = Pattern.compile(" ");

	/** Regex to parse out QueryString from HttpRequest */
	public static final Pattern QUERY_STRING_PATTERN = Pattern.compile("\\?");

	/** Regex to parse raw headers and body */
	public static final Pattern RAW_VALUE_PATTERN = Pattern
			.compile("\\r\\n\\r\\n");

	/** Regex to parse raw headers from body */
	public static final Pattern HEADERS_BODY_PATTERN = Pattern
			.compile("\\r\\n");

	/** Regex to parse header name and value */
	public static final Pattern HEADER_VALUE_PATTERN = Pattern.compile(": ");

	public HttpRequestDecoder() {

	}

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		try {
			return messageComplete(in) ? MessageDecoderResult.OK
					: MessageDecoderResult.NEED_DATA;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		LOGGER.warn("Http request not ok...");
		return MessageDecoderResult.NOT_OK;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		HttpRequest httpRequest = parseHttpRequest(in);
		// Return NEED_DATA if the body is not fully read.
		if (httpRequest == null) {
			LOGGER.warn("Http request need data...");
			return MessageDecoderResult.NEED_DATA;
		}

		out.write(httpRequest);
		LOGGER.debug("Http request message:\n" + httpRequest.toString());

		return MessageDecoderResult.OK;
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
	}

	private boolean messageComplete(IoBuffer in) throws Exception {
		int last = in.remaining() - 1;
		if (in.remaining() < 4) {
			return false;
		}

		// to speed up things we check if the Http request is a GET or POST
		if (in.get(0) == (byte) 'G' && in.get(1) == (byte) 'E'
				&& in.get(2) == (byte) 'T') {
			// Http GET request therefore the last 4 bytes should be 0x0D 0x0A
			// 0x0D 0x0A
			return in.get(last) == (byte) 0x0A
					&& in.get(last - 1) == (byte) 0x0D
					&& in.get(last - 2) == (byte) 0x0A
					&& in.get(last - 3) == (byte) 0x0D;
		} else if (in.get(0) == (byte) 'P' && in.get(1) == (byte) 'O'
				&& in.get(2) == (byte) 'S' && in.get(3) == (byte) 'T') {
			// Http POST request
			// first the position of the 0x0D 0x0A 0x0D 0x0A bytes
			int eoh = -1;
			for (int i = last; i > 2; i--) {
				if (in.get(i) == (byte) 0x0A && in.get(i - 1) == (byte) 0x0D
						&& in.get(i - 2) == (byte) 0x0A
						&& in.get(i - 3) == (byte) 0x0D) {
					eoh = i + 1;
					break;
				}
			}
			if (eoh == -1) {
				return false;
			}
			for (int i = 0; i < last; i++) {
				boolean found = false;
				for (int j = 0; j < CONTENT_LENGTH.length; j++) {
					if (in.get(i + j) != CONTENT_LENGTH[j]) {
						found = false;
						break;
					}
					found = true;
				}
				if (found) {
					// retrieve value from this position till next 0x0D 0x0A
					StringBuilder contentLength = new StringBuilder();
					for (int j = i + CONTENT_LENGTH.length; j < last; j++) {
						if (in.get(j) == 0x0D) {
							break;
						}
						contentLength.append(new String(
								new byte[] { in.get(j) }));
					}
					// if content-length worth of data has been received then
					// the message is complete
					return Integer.parseInt(contentLength.toString().trim())
							+ eoh == in.remaining();
				}
			}
		}

		// the message is not complete and we need more data
		return false;
	}

	private HttpRequest parseHttpRequest(final IoBuffer buffer) {
		int oldPosition = buffer.position();
		int last = buffer.limit() - 1;
		int eoh = -1;
		for (int i = last; i > 2; i--) {
			if (buffer.get(i) == (byte) 0x0A && buffer.get(i - 1) == (byte) 0x0D
					&& buffer.get(i - 2) == (byte) 0x0A
					&& buffer.get(i - 3) == (byte) 0x0D) {
				eoh = i + 1;
				break;
			}
		}
		
		if(eoh != -1) {
			String raw = null;
			try {
				raw = buffer.getString(eoh - oldPosition, defaultDecoder);
			} catch (CharacterCodingException e) {
				e.printStackTrace();
			}
			final String[] headersAndBody = RAW_VALUE_PATTERN.split(raw, -1);
			
			if (headersAndBody.length <= 1) {
				// we didn't receive the full HTTP head
				return null;
			}
			
			String[] headerFields = HEADERS_BODY_PATTERN.split(headersAndBody[0]);
			headerFields = ArrayUtil.dropFromEndWhile(headerFields, "");
			
			final String requestLine = headerFields[0];
			final Map<String, String> generalHeaders = new HashMap<String, String>();
			
			for (int i = 1; i < headerFields.length; i++) {
				final String[] header = HEADER_VALUE_PATTERN.split(headerFields[i]);
				generalHeaders.put(header[0], header[1]);
			}
			
			final String[] elements = REQUEST_LINE_PATTERN.split(requestLine);
			final HttpMethod method = HttpMethod.valueOf(elements[0]);
			final HttpVersion version = HttpVersion.fromString(elements[2]);
			final String[] pathFrags = QUERY_STRING_PATTERN.split(elements[1]);
			final String requestedPath = pathFrags[0];
			String queryString = pathFrags.length == 2 ? pathFrags[1] : "";
			if ((method == HttpMethod.POST) && (headersAndBody.length == 2)) {
				// body
//				queryString = headersAndBody[1];
			} else if ((method == HttpMethod.GET) && (pathFrags.length == 2)) {
				queryString = pathFrags[1];
			}
			
			
			HttpRequestMessage httpRequest = new HttpRequestMessage(version, method, requestedPath, queryString,
					generalHeaders);
			
			String contentLengthValue = generalHeaders.get(HttpHeaders.Key.CONTENT_LENGTH.toString());
			if(method == HttpMethod.POST && contentLengthValue != null) {
				int contentLength = Integer.valueOf(contentLengthValue);
				if(contentLength == 0) {
					return httpRequest;
				}
				
				try {
					String str = URLDecoder.decode(buffer.getString(contentLength, defaultDecoder), Charset.defaultCharset().name());
					// 如果转换的请求字符串参数为空，则反序列化Java对象
					if("".equals(str.trim())) {
						try {
							buffer.position(eoh);
							Object body = buffer.getObject();
							httpRequest.setBody(body);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
					else {
						httpRequest.setBody(str);
					}
				} catch (CharacterCodingException e1) {
//					e1.printStackTrace();
					LOGGER.info("Decode to character string failed, try to decode to java object ...");
					try {
						buffer.position(eoh);
						Object body = buffer.getObject();
						httpRequest.setBody(body);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
//			buffer.position(buffer.limit());
			
			return httpRequest;
		}

		return null;
	}
	
}
