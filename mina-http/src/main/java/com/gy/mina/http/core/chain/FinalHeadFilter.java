package com.gy.mina.http.core.chain;

import org.apache.mina.core.session.IoSession;

/**
 * 过滤器头，默认不实现任何功能，后期可扩展，用于做一些控制等操作
 * 
 * @author gyang
 *
 */
public class FinalHeadFilter implements IFilter {
	public FinalHeadFilter() {
	}
	
	@Override
	public void init() {

	}

	@Override
	public void doFilter(IoSession session, Object message, IFilterChain filterChain) {
		// do nothing
		filterChain.doFilter(session, message);
	}

	@Override
	public void destroy() {

	}

}
