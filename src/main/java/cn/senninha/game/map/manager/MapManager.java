package cn.senninha.game.map.manager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.game.map.BulletsObject;
import cn.senninha.game.map.Direction;
import cn.senninha.game.map.Grid;
import cn.senninha.game.map.GridStatus;
import cn.senninha.game.map.MapGround;
import cn.senninha.game.map.match.message.ResBattleResultMessage;
import cn.senninha.game.map.match.message.ResHitMessage;
import cn.senninha.game.map.message.ResBulletMessage;
import cn.senninha.game.map.message.ResMapResourceMessage;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
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
	 * 检测子弹信息
	 */
	public void checkBullets(){
		Collection<MapGround> collection = map.values();
		for(MapGround ground : collection){
			for(BulletsObject bullet : ground.getBulletsMap().values()){
				validateBullet(bullet);
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
			logger.debug("推送{}跑动信息完毕", client.getName());
		}
	}
	
	/**
	 * 校验子弹并且推送信息给移动端
	 * @param bullet
	 */
	private void validateBullet(BulletsObject bullet){
		int x = bullet.getX();
		int y = bullet.getY();
		byte direction = bullet.getDirection();
		MapGround mapGround = bullet.getMapGround();
		
		long currentTime = System.currentTimeMillis();
		int curGrid = MapHelper.convertPixelToGridIndex(x, y);
		
		int distance = (int) ((currentTime - bullet.getLastCheckTime()) / 10 * bullet.getSpeed());
		if(direction == Direction.NORTH.getDirection()) {//向上射击
			y = y - distance;
		}else if(direction == Direction.EAST.getDirection()) {//向右射击
			x = x + distance;
		}else if(direction == Direction.SOUTH.getDirection()) {//向下射击
			y = y + distance;
		}else if(direction == Direction.WEST.getDirection()) {//向西射击
			x = x - distance;
		}
		
		boolean outOfBound = MapHelper.needCorrect(x, y);//是否要消失,就是已经越过了地图
		if(outOfBound) {
			int[] xy = MapHelper.correctXY(x, y);
			x = xy[0];
			y = xy[1];
		}
		
		int gridIndex = MapHelper.convertPixelToGridIndex(x, y);
		//检查此次飞翔过程中是否对坦克造成伤害
		int shotSessionId = 0;	//被击中的sessionId
		int shotGrid = 0; 		//击中的格子
		if(direction == Direction.NORTH.getDirection()) {//向上射击
			for(int i = curGrid ; i >= gridIndex ; i = i - MapHelper.WIDTH_GRIDS) {
				shotSessionId = shoot(bullet, i);
				if(shotSessionId != 0) {
					shotGrid = i;
					break;
				}
			}
		}else if(direction == Direction.EAST.getDirection()) {//向右射击
			for(int i = curGrid ; i <= gridIndex ; i++) {
				shotSessionId = shoot(bullet, i);
				if(shotSessionId != 0) {
					shotGrid = i;
					break;
				}
			}
		}else if(direction == Direction.SOUTH.getDirection()) {//向下射击
			for(int i = curGrid ; i <= gridIndex ; i = i + MapHelper.WIDTH_GRIDS) {
				shotSessionId = shoot(bullet, i);
				if(shotSessionId != 0) {
					shotGrid = i;
					break;
				}
			}
		}else if(direction == Direction.WEST.getDirection()) {//向西射击
			for(int i = curGrid ; i >= gridIndex ; i--) {
				shotSessionId = shoot(bullet, i);
				if(shotSessionId != 0) {
					shotGrid = i;
					break;
				}
			}
		}
		
		if(shotSessionId == 0) {		//未击中，走正常的飞行过程
			notShotPushMessage(bullet, x, y, outOfBound);
		}else if(shotSessionId == -1){ //遇到障碍，消失
			notShotPushMessage(bullet, x, y, true);
			logger.debug("{}遇到障碍物，移除", bullet.getId());
		}else {							//击中了
			
			//消息推送和子弹去除
			shotAndPushMessage(bullet,shotGrid);
			logger.debug("{}击中了格子{}", bullet.getId(), gridIndex);
			
			//缺击中的服务端伤害处理,并除掉砖块
			boolean isOver = caculateHurt(bullet.getSourceSessionId(), shotGrid, bullet);
			
			if(isOver) {//游戏结束的处理
				this.removeMap(mapGround.getMapId()); //移除游戏Map;
			}
		}
		
		
	}
	
	/**
	 * 正常的子弹飞行，就是没有射中任何东西
	 * 如果一个子弹已经越过了边界，那么他的x，y都是0
	 * @param bullet
	 * @param x
	 * @param y
	 * @param disaper 消失，就是需要越界的
	 */
	private void notShotPushMessage(BulletsObject bullet, int x, int y, boolean disapper) {
		ResBulletMessage res = new ResBulletMessage();
		res.setId(bullet.getId());
		if(disapper) {
			res.setStatus(GridStatus.CAN_RUN.getStatus());
		}else {
			res.setStatus(GridStatus.SHELLS.getStatus());
			res.setX(x);
			res.setY(y);
		}
		
		for(Client client : bullet.getMapGround().getClientInMap().values()) {//推送消息
			client.pushMessage(res);
		}
		
		if(disapper) {
			/** 从地图中移除 **/
			bullet.getMapGround().getBulletsMap().remove(bullet.getId());
			bullet.setMapGround(null);
		}
	}
	
	/**
	 * 射中后，消息推送和处理这个子弹在服务端地图的状态
	 * @param bullet
	 * @param shotGrid
	 */
	private void shotAndPushMessage(BulletsObject bullet, int shotGrid) {
		ResBulletMessage res = new ResBulletMessage();
		res.setId(bullet.getId());
		res.setStatus(GridStatus.BOOM0.getStatus());
		int[] xy = MapHelper.convertGridIndexToPixel(shotGrid);
		res.setX(xy[0]);
		res.setY(xy[1]);
		
		for(Client client : bullet.getMapGround().getClientInMap().values()) {//推送消息
			client.pushMessage(res);
		}
	}
	
	/**
	 * 伤害处理
	 * @param shotIndex
	 * @param bulletsObject
	 */
	private boolean caculateHurt(int sourceId, int shotGrid, BulletsObject bulletsObject) {
		int sessionId = bulletsObject.getMapGround().getBlocks().get(shotGrid).getSessionId();
		Client client = ClientContainer.getInstance().getClient(sessionId);
		boolean isGameOver = false;
		if(client.beFire()) {
			//还活着
			logger.debug("{}挨了一枪，还活着", client.getName());
			ResHitMessage res = new ResHitMessage(sourceId, sessionId, client.getCanBeFire());
			bulletsObject.getMapGround().pushMessageInGround(res);
		}else {
			//gg了
			ResBattleResultMessage res = new ResBattleResultMessage(sourceId, sessionId, client.getName());
			bulletsObject.getMapGround().pushMessageInGround(res);
			logger.debug("{}挨了一枪后，gg了", client.getName());
			isGameOver = true;
		}
		
		/** 从地图中移除这个子弹 **/
		bulletsObject.getMapGround().getBulletsMap().remove(bulletsObject.getId());
		bulletsObject.setMapGround(null);
		return isGameOver;
	}
	
	/**
	 * 是否射中，如果射中，返回射中的sessionId,如果遇到障碍，返回-1
	 * @param bullet
	 * @return
	 */
	public int shoot(BulletsObject bullet, int curGrid) {
		int sessionId = 0;
		Grid grid = bullet.getMapGround().getBlocks().get(curGrid);
		if(grid.getSessionId() != 0) {
			Client client = bullet.getMapGround().getClientInMap().get(grid.getSessionId());
			if(client != null && client.getSessionId() != bullet.getSourceSessionId()) {
				logger.debug("{}被击中了：", client.getName());
				sessionId = grid.getSessionId();
			}
		}else if(grid.getStatus() == GridStatus.CAN_NOT_SHOT.getStatus()) {
			sessionId = -1;
		}
		return sessionId;
	}
	

	/**
	 * 把新加入的战斗加入
	 * @param mapId
	 * @param mapGround
	 */
	public void addMap(long mapId, MapGround mapGround) {
		map.put(mapId, mapGround);
		logger.debug("新加入战斗地图：{}", mapId);
	}
	
	/**
	 * 结束的战斗移除
	 * @param mapId
	 */
	public void removeMap(long mapId) {
		map.remove(mapId);
		logger.debug("战斗地图移除：{}", mapId);
	}
	
	/**
	 * 清理单方向掉线，判定胜负的问题
	 * @param sessionId 掉线的id
	 */
	public void removeOutLine(Client client) {
		if(client.getMapGround() != null) {//处于战斗中
			MapGround map = client.getMapGround();
			int winSessionId = 0;
			for(Client c : map.getClientInMap().values()) {
				if(c.getSessionId() != client.getSessionId()) {
					winSessionId = c.getSessionId();
				}
			}
			ResBattleResultMessage res = new ResBattleResultMessage(winSessionId, client.getSessionId(), client.getName());
			map.pushMessageInGround(res);
			this.removeMap(map.getMapId());//移除战斗
		}else {
			logger.debug("掉线设备不在战斗中");
		}
		client.offline();//设置下线
	}
	
	public void testEnterMap(Client[] clients) {
		MapGround map = new MapGround(System.currentTimeMillis(), MapHelper.generateGridRandom());
		this.addMap(map.getMapId(), map);
		int[][] bornGrid = new int[][]{{1,1},{20,15}, {1, 15}, {20, 1}}; //出生地点，后两个是AI_TANK
		int index = 0;
		for (Client client : clients) {
			client.enterMap(map);
			int x = bornGrid[index][0], y = bornGrid[index][1]; index++;//选择出生点
			client.updateLocation(x * MapHelper.PER_GRID_PIXEL  - MapHelper.PER_GRID_PIXEL/ 2, y * MapHelper.PER_GRID_PIXEL - MapHelper.PER_GRID_PIXEL/ 2);
			logger.debug("虚拟进入地图成功");
			logger.debug("推送阻挡信息成功");
		}
		
		for(Client client : clients){
			client.pushMessage(ResMapResourceMessage.valueOf(map.getBlocks()));
		}
	}

	/**
	 * 给AiManager调用的--
	 * @return
	 */
	public Map<Long, MapGround> getMap() {
		return map;
	}
	
}
