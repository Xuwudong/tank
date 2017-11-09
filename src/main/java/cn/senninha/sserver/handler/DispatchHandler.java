package cn.senninha.sserver.handler;


import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
import cn.senninha.sserver.lang.ByteBufUtil;
import cn.senninha.sserver.lang.codec.CodecFactory;
import cn.senninha.sserver.lang.dispatch.HandlerFactory;
import cn.senninha.sserver.lang.message.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.AttributeKey;

/**
 * 拆包并分发到对应的业务Handler
 * @author senninha on 2017年11月8日
 *
 */
public class DispatchHandler extends LengthFieldBasedFrameDecoder {

	public DispatchHandler(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, boolean failFast) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
	}

	
	/**
	 * decode()--->channelRead()
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		msg = (ByteBuf) super.decode(ctx, msg);
		if(msg == null){
			return null;
		}else{
			if (msg != null) {
				BaseMessage message = CodecFactory.getInstance().decode(ByteBufUtil.convert(msg));
				Integer sessionId = (Integer) (ctx.channel().attr(AttributeKey.valueOf("sessionId"))).get();
				if(sessionId == null){
					//登陆
					sessionId = 12580;
					Client client = new Client(sessionId, "senninha");
					ClientContainer.getInstance().addClient(client);
					
				}
				HandlerFactory.getInstance().dispatch(message, sessionId);
			}
		}
		return msg;
	}

}
