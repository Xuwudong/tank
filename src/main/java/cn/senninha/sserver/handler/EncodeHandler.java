package cn.senninha.sserver.handler;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.sserver.lang.ByteBufUtil;
import cn.senninha.sserver.lang.codec.CodecFactory;
import cn.senninha.sserver.lang.message.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EncodeHandler extends ChannelOutboundHandlerAdapter {
	private Logger logger = LoggerFactory.getLogger(EncodeHandler.class);
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		if (msg instanceof BaseMessage) {
			try {
				BaseMessage message = (BaseMessage) msg;
				ByteBuffer buffer = CodecFactory.getInstance().encode(message);
				ByteBuf buf = ByteBufUtil.convert(buffer);
				ctx.writeAndFlush(buf);
			} catch (Exception e) {
				logger.error("发送报文出错:", e);
			}
		}
	}
}
