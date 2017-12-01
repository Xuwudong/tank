package cn.senninha.game.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.senninha.game.map.message.ReqShellsMessage;
import cn.senninha.game.util.GameUtil;
import cn.senninha.sserver.client.Client;

public class MapGround {
	private long mapId;
	private List<Grid> blocks;
	private Map<Integer, Client> clientInMap;
	private Map<Integer, BulletsObject> bulletsMap;
	
	public MapGround(long mapId, List<Grid> blocks) {
		super();
		this.mapId = mapId;
		this.blocks = blocks;
		clientInMap = new HashMap<Integer, Client>();
		bulletsMap = new HashMap<>();
	}
	
	public long getMapId() {
		return mapId;
	}
	public void setMapId(long mapId) {
		this.mapId = mapId;
	}
	public List<Grid> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<Grid> blocks) {
		this.blocks = blocks;
	}
	public Map<Integer, Client> getClientInMap() {
		return clientInMap;
	}
	public void setClientInMap(Map<Integer, Client> clientInMap) {
		this.clientInMap = clientInMap;
	}

	/**
	 * 地图中的子弹
	 * @return
	 */
	public Map<Integer, BulletsObject> getBulletsMap() {
		return bulletsMap;
	}
	
	/**
	 * 移除地图中的子弹
	 * @param id
	 */
	public void removeBullet(int id) {
		bulletsMap.remove(id);
	}
	
	/**
	 * 添加子弹进地图
	 * @return
	 */
	public void addBullets(ReqShellsMessage res, int speed) {
		BulletsObject b = new BulletsObject();
		int id = GameUtil.generateIntegerId();
		b.setId(id);
		b.setX(res.getX());
		b.setY(res.getY());
		b.setSourceSessionId(res.getSourceSessionId());
		b.setMapGround(this);
		b.setDirection(res.getDirection());
		b.setLastCheckTime(System.currentTimeMillis());
		b.setSpeed(speed);
		bulletsMap.put(id, b);
	}


	/**
	 * 地图中的子弹
	 * @param bulletsMap
	 */
	public void setBulletsMap(Map<Integer, BulletsObject> bulletsMap) {
		this.bulletsMap = bulletsMap;
	}
	
	
}
