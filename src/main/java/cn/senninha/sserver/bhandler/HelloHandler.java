package cn.senninha.sserver.bhandler;

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
	@MessageInvoke(cmd = 7)
	public void hello(int sessionId, BaseMessage message){
		System.out.println(sessionId + message.toString());
	}
}
