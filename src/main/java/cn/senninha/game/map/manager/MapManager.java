package cn.senninha.game.map.manager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.game.map.MapGround;
import cn.senninha.game.map.message.ResMapResourceMessage;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.lang.message.BaseMessage;

/**
 * 地图管理类
 * @author senninha
 *
 */
public class MapManager {
	private Logger logger = LoggerFactory.getLogger(MapManager.class);
	private static MapManager instance;
	private Map<Long, MapGround> map = new ConcurrentHashMap<>();
	
	private MapManager() {
		
	}
	
	public static MapManager getInstance() {
		if(instance == null) {
			synchronized (MapManager.class) {
				if(instance == null) {
				instance = new MapManager();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 不要调用。。。验证跑步的
	 */
	public void run() {
		Collection<MapGround> collection = map.values();
		for(MapGround ground : collection) {
			for(Client client : ground.getClientInMap().values()) {
				validateRun(client);
			}
		}
	}
	
	/**
	 * 校验移动并推送给对应的客户端
	 * @param client
	 */
	private void validateRun(Client client) {
		if(client.getMapGround() != null) {	//表示还在战斗中
			BaseMessage res = MapHelper.validateRun(client);
			if(res == null) {
				return;
			}
			for(Client c : client.getMapGround().getClientInMap().values()) {
				c.pushMessage(res);
			}
			logger.error("推送{}跑动信息完毕", client.getName());
		}
	}
	
	/**
	 * 把新加入的战斗加入
	 * @param mapId
	 * @param mapGround
	 */
	public void addMap(long mapId, MapGround mapGround) {
		map.put(mapId, mapGround);
		logger.error("新加入战斗地图：{}", mapId);
	}
	
	/**
	 * 结束的战斗移除
	 * @param mapId
	 */
	public void removeMap(long mapId) {
		map.remove(mapId);
		logger.error("战斗地图移除：{}", mapId);
	}
	
	public void testEnterMap(Client client) {
		MapGround map = new MapGround(System.currentTimeMillis(), MapHelper.generateGridRandom());
		this.addMap(map.getMapId(), map);
		client.enterMap(map);
		int[] bornGrid = MapHelper.getRandomBorn(map);
		int x = bornGrid[0], y = bornGrid[1];
		client.updateLocation(x * MapHelper.PER_GRID_PIXEL / 2, y * MapHelper.PER_GRID_PIXEL / 2);
		logger.error("虚拟进入地图成功");
		
		client.pushMessage(ResMapResourceMessage.valueOf(map.getBlocks()));
		logger.error("推送阻挡信息成功");
	}
}
