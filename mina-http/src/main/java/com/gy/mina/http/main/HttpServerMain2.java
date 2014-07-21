package com.gy.mina.http.main;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.gy.mina.http.core.coder.HttpServerProtocolCodecFactory;
import com.gy.mina.http.core.handler.HttpServerHandler;

public class HttpServerMain2 {

	private static final int DEFAULT_PORT = 8080;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IoAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("HttpProcolFilter", new ProtocolCodecFilter(new HttpServerProtocolCodecFactory()));
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.setHandler(new HttpServerHandler());
		
        try {
        	InetSocketAddress socketAddress = new InetSocketAddress(DEFAULT_PORT);
			acceptor.bind(socketAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}

        System.out.println("Server now listening on port " + DEFAULT_PORT);
	}

}
