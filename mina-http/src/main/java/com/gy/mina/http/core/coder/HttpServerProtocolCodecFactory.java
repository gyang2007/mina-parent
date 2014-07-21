  package com.gy.mina.http.core.coder;
  
  import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.gy.mina.model.HttpResponseMessage;
  
  public class HttpServerProtocolCodecFactory extends
          DemuxingProtocolCodecFactory {
      public HttpServerProtocolCodecFactory() {
          super.addMessageDecoder(HttpRequestDecoder.class);
          super.addMessageEncoder(HttpResponseMessage.class, HttpResponseEncoder.class);
      }
  }