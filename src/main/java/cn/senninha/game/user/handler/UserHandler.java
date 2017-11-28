package cn.senninha.game.user.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.game.map.manager.MapManager;
import cn.senninha.game.user.UserManager;
import cn.senninha.game.user.message.ReqHeartbeatMessge;
import cn.senninha.game.user.message.ReqLoginMessage;
import cn.senninha.game.user.message.ResHeartbeatMessge;
import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
import cn.senninha.sserver.lang.dispatch.MessageHandler;
import cn.senninha.sserver.lang.dispatch.MessageInvoke;
import cn.senninha.sserver.lang.message.BaseMessage;

@MessageHandler
public class UserHandler {
	private Logger logger = LoggerFactory.getLogger(UserHandler.class);
	
	@MessageInvoke(cmd = CmdConstant.LOGIN_REQ)
	public void login(int sessionId, BaseMessage message) {
		logger.error("用户 {} 尝试登陆", message.toString());
		UserManager.getInstance().login(sessionId, (ReqLoginMessage) message);	
		
		MapManager.getInstance().testEnterMap(ClientContainer.getInstance().getClient(sessionId));
	}
	
	@MessageInvoke(cmd = CmdConstant.HEART_REQ)
	public void heartbeat(int sessionId, BaseMessage message) {
		if(message instanceof ReqHeartbeatMessge) {
			ReqHeartbeatMessge m = (ReqHeartbeatMessge) message;
			ResHeartbeatMessge res = new ResHeartbeatMessge(m);
			Client client = ClientContainer.getInstance().getClient(sessionId);
			logger.error("收到心跳：{}", m.getTime());
			if(client != null) {
				client.pushMessage(res);
			}
		}
	}
}
