package cn.senninha.game.map.match;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
import cn.senninha.sserver.lang.dispatch.MessageHandler;
import cn.senninha.sserver.lang.dispatch.MessageInvoke;
import cn.senninha.sserver.lang.message.BaseMessage;

/**
 * 匹配比赛
 * @author senninha
 *
 */
@MessageHandler
public class MatchHandler {
	private Logger logger = LoggerFactory.getLogger(MatchHandler.class);
	
	@MessageInvoke(cmd = CmdConstant.REQ_MATCH)
	public void reqMatch(int sessionId, BaseMessage message) {
		Client client = ClientContainer.getInstance().getClient(sessionId);
		if(client != null) {
			MatchManager.getInstance().reqMatch(client);
		}else {
			logger.error("错误！！！，client为null");
		}
	}
}
