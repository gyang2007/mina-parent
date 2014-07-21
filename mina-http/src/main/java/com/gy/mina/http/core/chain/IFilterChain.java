package com.gy.mina.http.core.chain;

import org.apache.mina.core.session.IoSession;

public interface IFilterChain {
	boolean addFilter(IFilter filter);
	boolean removeFilter(IFilter filter);
	void doFilter(IoSession session, Object message);
	
	public interface Entry {
		IFilter getFilter();
		Entry getPrevEntry();
		Entry getNextEntry();
	}
}
