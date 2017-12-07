package cn.senninha.game.ai;

import java.util.Map;

import cn.senninha.game.map.MapGround;
import cn.senninha.game.map.manager.MapManager;
import cn.senninha.sserver.client.Client;

/**
 * AI管理类
 * @author senninha
 *
 */
public class AiManager {
	private static AiManager instance;
	private Map<Long, MapGround> maps;
	
	private AiManager() {
		maps = MapManager.getInstance().getMap();
	}
	
	/**
	 * 单例
	 * @return
	 */
	public static AiManager getInstance() {
		if(instance == null) {
			synchronized (AiManager.class) {
				if(instance == null) {
					instance = new AiManager();
				}
			}
		}
		return instance;
	}
	/**
	 * AI检测任务
	 */
	public void check() {
		for(MapGround ground : maps.values()) {
			checkAI(ground);
		}
	}
	
	/**
	 * 检测AI与行走
	 * @param ground
	 */
	private void checkAI(MapGround ground) {
		for(Client ai : ground.getClientInMap().values()) {
			if(!ai.isAi()) {	//不是ai
				continue;
			}
			
			/** 进行AI检测 **/
			
		}
	}
}
