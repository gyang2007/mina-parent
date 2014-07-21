package com.gy.mina.http.core.chain;

import org.apache.mina.core.session.IoSession;

import com.gy.mina.http.core.HttpDispatcher;

/**
 * 过滤器末端，用于处理、分发HTTP请求
 * 
 * @author gyang
 *
 */
public class FinalTailFilter implements IFilter {
	private HttpDispatcher dispatcher;
	
	public FinalTailFilter() {
		dispatcher = new HttpDispatcher();
	}
	
	@Override
	public void init() {

	}

	@Override
	public void doFilter(IoSession session, Object message, IFilterChain filterChain) {
		// 分发HTTP请求
		dispatcher.dispatch(session, message);
	}

	@Override
	public void destroy() {

	}

}
