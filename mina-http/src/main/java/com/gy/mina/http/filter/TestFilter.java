package com.gy.mina.http.filter;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gy.mina.http.core.chain.IFilter;
import com.gy.mina.http.core.chain.IFilterChain;

/**
 * 测试过滤器
 * 
 * @author gyang
 *
 */
public class TestFilter implements IFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestFilter.class);

	@Override
	public void init() {
		System.out.println("TestFilter:init");
	}

	@Override
	public void doFilter(IoSession session, Object message,
			IFilterChain filterChain) {
		LOGGER.info("I'm a test filter~~~");
		
		filterChain.doFilter(session, message);
	}

	@Override
	public void destroy() {
		System.out.println("TestFilter:destroy");
	}

}
