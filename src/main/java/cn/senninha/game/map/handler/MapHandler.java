package cn.senninha.game.map.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.game.GameStatus;
import cn.senninha.game.map.MapGround;
import cn.senninha.game.map.Steps;
import cn.senninha.game.map.manager.MapHelper;
import cn.senninha.game.map.message.ReqRunMessage;
import cn.senninha.game.map.message.ReqShellsMessage;
import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
import cn.senninha.sserver.lang.dispatch.MessageHandler;
import cn.senninha.sserver.lang.dispatch.MessageInvoke;
import cn.senninha.sserver.lang.message.BaseMessage;

@MessageHandler
public class MapHandler {
	private Logger logger = LoggerFactory.getLogger(MapHandler.class);
	@MessageInvoke(cmd = CmdConstant.RUN_REQ)
	public void reqRun(int sessionId, BaseMessage message) {
		Client client = ClientContainer.getInstance().getClient(sessionId);
		if(client != null) {
			//进行走路校验
			client.addSteps(Steps.valueOf((ReqRunMessage)(message)));
		}
	}
	
	@MessageInvoke(cmd = CmdConstant.REQ_SHELLS)
	public void reqFire(int sessionId, BaseMessage message){
		Client client = ClientContainer.getInstance().getClient(sessionId);
		if(client == null){
			return;
		}
		ReqShellsMessage req = (ReqShellsMessage) message;
		boolean canShot = MapHelper.validateCanFire(client);
		if(canShot){
			MapGround ground = client.getMapGround();
			if(ground != null){
				if(!MapHelper.corrcetFireSource(req)) {
					logger.error("射击源未校验通过", req);
				}else {
					ground.addBullets(req, GameStatus.GAME_COMMON_BULLET_SPEED.getValue());
					logger.debug("射击成功：{}", client.getName());
				}
			}else{
				logger.error("不处于战斗中，无法射击：{}", client.getName());
			}
			
		}else{
			logger.error("当前射击过于频繁:{}", client.getName());
		}
	}
}
