package com.gy.mina.http.core.handler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gy.mina.http.core.chain.FilterChainBuider;
import com.gy.mina.http.core.chain.FilterChainImpl;
import com.gy.mina.http.core.chain.IFilter;
import com.gy.mina.http.core.chain.IFilterChain;
import com.gy.mina.model.HttpResponseMessage;

public class HttpServerHandler extends IoHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);
	
	/**
	 * 默认HTTP连接空闲时间
	 */
	private static final int IDLE_TIME_DEFAULT = 5;
	
	private final FilterChainBuider chainBuilder = new FilterChainBuider();
	
	/**
	 * 添加自定义过滤器
	 * 
	 * @param filter
	 */
	public void addFilter(IFilter filter) {
		chainBuilder.addFilter(filter);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE_TIME_DEFAULT);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		session.close(false);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		session.close(false);
		LOGGER.error("Http request error:\n" + cause.toString());
		
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		/**
		 * 调用过滤器链
		 */
		IFilterChain filterChain = new FilterChainImpl();
		chainBuilder.buildFilterChain(filterChain);
		filterChain.doFilter(session, message);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		if(message instanceof HttpResponseMessage) {
			HttpResponseMessage msg = (HttpResponseMessage) message;
			if(!msg.isKeepAlive()) {
				session.close(true);
				
				LOGGER.info("Http server close session ...");
			}
		}
	}
}
