package cn.senninha.sserver.handler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.game.map.manager.MapManager;
import cn.senninha.game.user.message.ReqHeartbeatMessge;
import cn.senninha.game.user.message.ReqLoginMessage;
import cn.senninha.game.user.message.ResHeartbeatMessge;
import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
import cn.senninha.sserver.lang.ByteBufUtil;
import cn.senninha.sserver.lang.codec.CodecFactory;
import cn.senninha.sserver.lang.dispatch.HandleContext;
import cn.senninha.sserver.lang.dispatch.Task;
import cn.senninha.sserver.lang.message.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

/**
 * 拆包并分发到对应的业务Handler
 * 
 * @author senninha on 2017年11月8日
 *
 */
public class DispatchHandler extends LengthFieldBasedFrameDecoder {
	private Logger logger = LoggerFactory.getLogger(DispatchHandler.class);

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
		if (msg == null) {
			return null;
		} else {
			if (msg != null) {
				BaseMessage message = CodecFactory.getInstance().decode(ByteBufUtil.convert(msg));
				Integer sessionId = (Integer) (ctx.channel().attr(AttributeKey.valueOf("sessionId"))).get();
				if (sessionId == null) {
					if (message.getCmd() == CmdConstant.LOGIN_REQ) {
						// 登陆
						ReqLoginMessage req = (ReqLoginMessage) message;
						sessionId = req.getSessionId();
						Client client = new Client(sessionId, req.getUsername(), ctx);
						ClientContainer.getInstance().addClient(client);
					} else {
						return null;
					}
				}
				if(message instanceof ReqHeartbeatMessge) {
					sendHeartbeat(message, sessionId);
					return null;
				}
				HandleContext.getInstance().dispatch(sessionId, message);
			}
		}
		return null;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Integer sessionId = (Integer) (ctx.channel().attr(AttributeKey.valueOf("sessionId"))).get();
		if (sessionId == null) {
			logger.error("匿名连接掉线：{}", ctx.channel().remoteAddress().toString());
		} else {
			Client client = ClientContainer.getInstance().remove(sessionId);
			if (client != null) {
				logger.error("用户{}离线", client.getName());
			}
			MapManager.getInstance().removeOutLine(client);//清理掉线数据
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
			case ALL_IDLE:
				disconnect(ctx);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param ctx
	 * @param sessionId
	 */
	private void disconnect(ChannelHandlerContext ctx) {
		Integer sessionId = (Integer) (ctx.channel().attr(AttributeKey.valueOf("sessionId"))).get();
		ctx.disconnect();
		if(sessionId == null) {
			return;
		}
		final Client client = ClientContainer.getInstance().remove(sessionId);
		
		//掉线处理回归场景线程
		HandleContext.getInstance().addCommand(0, new Task(0, false, 0, TimeUnit.MILLISECONDS, new Runnable() {
			
			@Override
			public void run() {
				MapManager.getInstance().removeOutLine(client);
			}
		}));
		
		
		/**
		 * 清理掉战斗
		 */
		logger.error("心跳超时掉线：{}", sessionId);
	}
	
	private void sendHeartbeat(BaseMessage message, int sessionId) {
		ReqHeartbeatMessge m = (ReqHeartbeatMessge) message;
		ResHeartbeatMessge res = new ResHeartbeatMessge(m);
		Client client = ClientContainer.getInstance().getClient(sessionId);
		logger.error("ping{}", res.getCurrent() - m.getTime());
		if(client != null) {
			client.pushMessage(res);
		}
	}

}
