package com.gy.mina.http.core.chain;

import org.apache.mina.core.session.IoSession;

/**
 * 过滤器链的默认实现，使用了双向链表将过滤器前后相连
 * 
 * @author gyang
 *
 */
public class FilterChainImpl implements IFilterChain {
	
	private Entry current;
	private Entry head;
	private Entry tail;
	
	public FilterChainImpl() {
		head = new Entry(new FinalHeadFilter(), null, null);
		tail = new Entry(new FinalTailFilter(), head, null);
		head.nextEntry = tail;
		
		current = head;
	}

	@Override
	public boolean addFilter(IFilter filter) {
		Entry last = tail;
		Entry prevLast = tail.prevEntry;
		
		Entry newEntry = new Entry(filter, prevLast, last);
		prevLast.nextEntry = newEntry;
		last.prevEntry = newEntry;
		
		return true;
	}

	@Override
	public boolean removeFilter(IFilter filter) {
		boolean f = false;
		
		Entry tmp = head;
		for(; tmp != null; tmp = tmp.nextEntry) {
			if(filter == tmp.getFilter()) {
				Entry prev = tmp.prevEntry;
				Entry next = tmp.nextEntry;
				
				prev.nextEntry = next;
				next.prevEntry = prev;
				
				f = true;
				
				break;
			}
		}
		
		return f;
	}
	
	@Override
	public void doFilter(IoSession session, Object message) {
		IFilter filter = current.getFilter();
		current = current.nextEntry;
		filter.init();
		filter.doFilter(session, message, this);
		filter.destroy();
	}

	static class Entry implements IFilterChain.Entry {
		private IFilter filter;
		private Entry prevEntry;
		private Entry nextEntry;
		
		public Entry(IFilter filter, Entry prevEntry, Entry nextEntry) {
			this.filter = filter;
			this.prevEntry = prevEntry;
			this.nextEntry = nextEntry;
		}

		@Override
		public IFilter getFilter() {
			return filter;
		}

		@Override
		public Entry getPrevEntry() {
			return prevEntry;
		}

		@Override
		public Entry getNextEntry() {
			return nextEntry;
		}
	}

}
