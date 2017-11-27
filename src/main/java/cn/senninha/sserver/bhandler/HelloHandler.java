package cn.senninha.sserver.bhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
import cn.senninha.sserver.handler.EncodeHandler;
import cn.senninha.sserver.lang.dispatch.MessageHandler;
import cn.senninha.sserver.lang.dispatch.MessageInvoke;
import cn.senninha.sserver.lang.message.BaseMessage;

/**
 * 业务Handler，类上注解MessageHandler,对应方法注解是MessageInvoke，并加上int，BaseMessage(必须按这个顺序)即可。
 * @author senninha on 2017年11月7日
 *
 */
@MessageHandler
public class HelloHandler {
	private Logger logger = LoggerFactory.getLogger(EncodeHandler.class);

	@MessageInvoke(cmd = 7)
	public void hello(int sessionId, BaseMessage message){
		System.out.println(sessionId + message.toString());
		Client client = ClientContainer.getInstance().getClient(sessionId);
		client.pushMessage(message);
		logger.debug("收到如下信息:[]", message);
	}
}
