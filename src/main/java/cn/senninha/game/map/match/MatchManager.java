package cn.senninha.game.map.match;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.game.GameStatus;
import cn.senninha.game.PromptInfo;
import cn.senninha.game.map.manager.MapManager;
import cn.senninha.game.map.match.message.ResMatchMessage;
import cn.senninha.sserver.client.Client;

/**
 * 匹配管理
 * @author senninha
 *
 */
public class MatchManager {
	private Logger logger = LoggerFactory.getLogger(MatchManager.class);
	private static MatchManager instance = null;
	private Map<Integer, Client> oneVoneMap;	//1v1的匹配队列
	
	private MatchManager() {
		oneVoneMap = new LinkedHashMap<>(8);
	}
	
	public static MatchManager getInstance() {
		if(instance == null) {
			synchronized (MatchManager.class) {
				if(instance == null) {
					instance = new MatchManager();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 请求匹配
	 * @param client
	 */
	public void reqMatch(Client client) {
		if(oneVoneMap.get(client.getSessionId()) != null) {//已经在匹配了
			return;
		}
		
		Client another = null;
		Iterator<Client> iterator = oneVoneMap.values().iterator();
		while(iterator.hasNext()) {
			another = iterator.next();
			iterator.remove();
			if(another.isOnline()) {
				break;
			}
		}
		
		if(another != null && another.isOnline()) {
			//匹配成功
			logger.error("匹配成功");
			
			/** 设置生命值 **/
			client.setCanBeFire(GameStatus.GAME_LIVE.getValue());
			another.setCanBeFire(GameStatus.GAME_LIVE.getValue());
			
			
			MapManager.getInstance().testEnterMap(new Client[] {client, another});
		}else {
			oneVoneMap.put(client.getSessionId(), client);
			client.pushMessage(ResMatchMessage.valueOf(PromptInfo.WAIT_TO_MATCH.getPmt()));
			logger.error("{}等待匹配中", client.getName());
		}
	}
}
