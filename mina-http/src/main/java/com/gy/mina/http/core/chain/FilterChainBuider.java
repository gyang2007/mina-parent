package com.gy.mina.http.core.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gy.mina.http.core.ResourceManager;

/**
 * 用于构建过滤器链的辅助类
 * 
 * @author gyang
 *
 */
public class FilterChainBuider {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterChainBuider.class);
	
	private ReentrantLock lock = new ReentrantLock();
	
	private List<IFilter> filters = new ArrayList<IFilter>();
	
	public FilterChainBuider() {
		List<Class<?>> filterClasses = ResourceManager.getInstance().getFilterResourceManager().getFilterClasses();
		for(Class<?> c : filterClasses) {
			try {
				filters.add((IFilter) c.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<IFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<IFilter> filters) {
		this.filters = filters;
	}
	
	public void addFilter(IFilter filter) {
		this.filters.add(filter);
	}

	public void buildFilterChain(IFilterChain filterChain) {
/*		try{
			lock.lock();
			for(IFilter filter : filters) {
				filterChain.addFilter(filter);
			}
		} finally {
			lock.unlock();
		}*/
		for(IFilter filter : filters) {
			filterChain.addFilter(filter);
		}
	}
}
