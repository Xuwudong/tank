package cn.senninha.game.map.handler;

import cn.senninha.game.map.Steps;
import cn.senninha.game.map.message.ReqRunMessage;
import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
import cn.senninha.sserver.lang.dispatch.MessageHandler;
import cn.senninha.sserver.lang.dispatch.MessageInvoke;
import cn.senninha.sserver.lang.message.BaseMessage;

@MessageHandler
public class MapHandler {
	@MessageInvoke(cmd = CmdConstant.RUN_REQ)
	public void reqRun(int sessionId, BaseMessage message) {
		Client client = ClientContainer.getInstance().getClient(sessionId);
		if(client != null) {
			//进行走路校验
			client.addSteps(Steps.valueOf((ReqRunMessage)(message)));
		}
	}
}
