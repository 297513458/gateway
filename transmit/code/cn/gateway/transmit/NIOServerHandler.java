package cn.gateway.transmit;

import java.util.Date;

import cn.gateway.logger.Logger;
import cn.gateway.logger.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;

public class NIOServerHandler extends ChannelHandlerAdapter { // (1)
	private static final Logger log = LoggerFactory.getLogger(NIOServerHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		String method = null;
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			method = request.method().name().toString();
			log.info("请求协议:" + request.protocolVersion());
				log.info("方法:" + request.method());
				log.info("路径:" + request.uri());
				log.info("请求头:" + request.headers());
		}
		if (msg instanceof HttpContent) {
			HttpContent httpcontent = (HttpContent) msg;
			ByteBuf byteBuf = httpcontent.content();
			String body = byteBuf.toString(io.netty.util.CharsetUtil.UTF_8);
			if ("post".equalsIgnoreCase(method))
					log.info("请求body:" + body);
			ByteBuf res = Unpooled.wrappedBuffer("{\"message\":\"收到了\",\"code\":200}".getBytes());
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, res);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8");
			ctx.write(response);
			ctx.flush();
			ctx.close();
		}
		ReferenceCountUtil.release(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		log.info("接入:" + ctx.channel().localAddress().toString() + " channelActive");
		// 通知您已经链接上客户端
		String str = "您已经开启与服务端链接" + " " + new Date() + " " + ctx.channel().localAddress();
		ByteBuf buf = Unpooled.buffer(str.getBytes().length);
		buf.writeBytes(str.getBytes());
		ctx.writeAndFlush(str);
	}
}