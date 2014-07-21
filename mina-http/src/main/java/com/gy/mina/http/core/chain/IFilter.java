package com.gy.mina.http.core.chain;

import org.apache.mina.core.session.IoSession;

public interface IFilter {
	/**
	 * 过滤器的初始化操作
	 */
	void init();
	/**
	 * 过滤器的具体处理逻辑
	 * 
	 * @param session
	 * @param message
	 * @param filterChain
	 */
	void doFilter(IoSession session, Object message, IFilterChain filterChain);
	/**
	 * 
	 */
	void destroy();
}
