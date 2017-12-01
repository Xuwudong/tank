package cn.senninha.game.map.match;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private LinkedList<Client> clientToMatch;
	
	private MatchManager() {
		clientToMatch = new LinkedList<>();
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
		Client another = clientToMatch.poll();
		if(another != null && another.isOnline()) {
			//匹配成功
			logger.error("匹配成功");
			MapManager.getInstance().testEnterMap(new Client[] {client, another});
		}else {
			clientToMatch.push(client);
			client.pushMessage(ResMatchMessage.valueOf(PromptInfo.WAIT_TO_MATCH.getPmt()));
			logger.error("{}等待匹配中", client.getName());
		}
	}
}
