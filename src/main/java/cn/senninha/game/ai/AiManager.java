package cn.senninha.game.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.game.GameStatus;
import cn.senninha.game.PromptInfo;
import cn.senninha.game.ai.message.ResAiHurtMessage;
import cn.senninha.game.ai.message.ResAiKillMessage;
import cn.senninha.game.map.Direction;
import cn.senninha.game.map.Grid;
import cn.senninha.game.map.MapGround;
import cn.senninha.game.map.Steps;
import cn.senninha.game.map.manager.MapHelper;
import cn.senninha.game.map.manager.MapManager;
import cn.senninha.game.map.message.ReqShellsMessage;
import cn.senninha.game.map.util.ASNode;
import cn.senninha.game.map.util.ASUtil;
import cn.senninha.sserver.client.AiTank;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;

/**
 * AI管理类
 * 
 * @author senninha
 *
 */
public class AiManager {
	private Logger logger = LoggerFactory.getLogger(AiManager.class);
	private static AiManager instance;
	private Map<Long, MapGround> maps;

	private AiManager() {
		maps = MapManager.getInstance().getMap();
	}

	/**
	 * 单例
	 * 
	 * @return
	 */
	public static AiManager getInstance() {
		if (instance == null) {
			synchronized (AiManager.class) {
				if (instance == null) {
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
		for (MapGround ground : maps.values()) {
			checkAI(ground);
			aiShot(ground);
		}
	}

	/**
	 * 检测AI与行走
	 * 
	 * @param ground
	 */
	private void checkAI(MapGround ground) {
		for (Client tem : ground.getClientInMap().values()) {
			if (tem instanceof AiTank) { // 是AI
				AiTank ai = (AiTank) tem;

//				boolean canHurt = ai.isCoolDown();
//				
//				if(!canHurt) {	//未冷却
//					continue;
//				}
//				
//				int isHrut = aiHurt(ai);
//				if(isHrut == 1) { //如果已经冷却并且造成了伤害
//					ai.clearAllSteps();
//					aiHurtPushMessage(ai);
//					ai.setCoolDown();	 //重新设置冷却时间
//					continue;
//				}else if(isHrut == 2){//已经死亡
//					break;
//				}
				
				List<Grid> grids = ground.getBlocks();
				int gridIndex = MapHelper.convertPixelToGridIndex(ai.getX(), ai.getY());

				Client targetClient = ground.getClientInMap().get(ai.getAiTarget());
				int targetIndex = MapHelper.convertPixelToGridIndex(targetClient.getX(), targetClient.getY());

				if (!ai.needFindRoad(targetIndex) && ai.getHeadStepButNotRemove() != null) { // 目标未改变位置,或者ai还有要走的路了，无需再次寻路
					continue;
				}
				// 设置上一次的寻路路径
				ai.setLastTargetGridIndex(targetIndex);

				Grid source = grids.get(gridIndex);
				Grid target = grids.get(targetIndex);

				// 寻路,特地把起点和终点反过来是因为寻路结果是从终点到起点的--
				ASNode road = ASUtil.aStar(grids, target, source, MapHelper.WIDTH_GRIDS, MapHelper.HEIGHT_GRIDS);
				if(road == null) { //无法寻找到路
					logger.debug("{}无法寻找{}", ai.getSessionId(), targetClient.getSessionId());
					continue;
				}
				List<Steps> steps = convertASNodeToSteps(road);
				ai.clearAllSteps();
				ai.addSteps(steps);
				logger.debug("ai{}追踪{}，寻路结果：{}", ai.getSessionId(), targetClient.getSessionId(), road.toString());
			}
		}
	}
	
	/**
	 * ai射击
	 * @param ground
	 */
	private void aiShot(MapGround ground) {
		for(Client client : ground.getClientInMap().values()) {
			if(client instanceof AiTank) {
				AiTank tank = (AiTank) client;
				int x = tank.getX();			//像素
				int y = tank.getY();
				
				int direction = tank.getDirection();
				int gridIndex = MapHelper.convertPixelToGridIndex(x, y);
				int[] indexIncrease = new int[] {-MapHelper.WIDTH_GRIDS, 1, MapHelper.WIDTH_GRIDS, -1};
				int[] boundary = new int[] {0, (gridIndex / MapHelper.WIDTH_GRIDS + 1) * MapHelper.WIDTH_GRIDS, MapHelper.TOTAL_GRIDS, (gridIndex / MapHelper.WIDTH_GRIDS - 1) * MapHelper.WIDTH_GRIDS};
				if(boundary[3] <= 0) {
					boundary[3] = 0;
				}
				boolean stop = false;
				
				while(true) {
					gridIndex = gridIndex + indexIncrease[direction];
					switch(direction) {
						case 0:
							if(gridIndex < boundary[0]) {
								stop = true;
							}
								break;
							
						case 1:
							if(gridIndex >= boundary[1]) {
								stop = true;
							}
							break;
						case 2:
							if(gridIndex >= boundary[2]) {
								stop = true;
							}
							break;
						case 3:
							if(gridIndex < boundary[3]) {
								stop = true;
							}
							break;
					}
					
					if(stop) {
						break;
					}
					
					int targetSessionId = ground.getBlocks().get(gridIndex).getSessionId();
					Client target = ClientContainer.getInstance().getClient(targetSessionId);
					if(target != null) {//射击
						long cur = System.currentTimeMillis();
						if (cur - tank.getFireTime() >= tank.getFireIntervel()) {
							tank.setFireTime(cur);
							ground.addBullets(ReqShellsMessage.valueOf(tank),
									GameStatus.GAME_COMMON_BULLET_SPEED.getValue() / 2);
							logger.debug("ai射击完毕:{}", tank.getSessionId());
						}
						return;
					}
							
				}
			}
		}
	}

	/**
	 * 转化ASNode为Step,如果距离小于1,直接返回空的List,返回的路子是从当前点到目标点的前一个格子，特地忽略了目标点～
	 * 
	 * @param road
	 * @return
	 */
	private List<Steps> convertASNodeToSteps(ASNode road) {
		List<Steps> steps = new ArrayList<>();
		ASNode parent = null;
		while((parent = road.getParent()) != null) {
			Grid roadStep = road.getValue();
			Grid parentStep = parent.getValue();
			
			for(int i = 0 ; i < MapHelper.PER_GRID_PIXEL / GameStatus.GAME_AI_PER_STEP.getValue() ; i++) {
				steps.add(getSteps(roadStep, parentStep));
			}
			
			road = parent;
		}
		
		if(steps.size() == MapHelper.PER_GRID_PIXEL / GameStatus.GAME_AI_PER_STEP.getValue()) {
			steps.clear();
		}
		return steps;
	}
	
	
	private Steps getSteps(Grid roadStep, Grid parentStep) {
		Steps step = new Steps();
		step.setStep((byte)GameStatus.GAME_AI_PER_STEP.getValue());
		
		if(parentStep.getX() > roadStep.getX()) { 			//向右边移动
			step.setDirection(Direction.EAST.getDirection());
		}else if(parentStep.getX() < roadStep.getX()) {		//向西边移动
			step.setDirection(Direction.WEST.getDirection());
		}else if(parentStep.getY() > roadStep.getY()) {	//向下移动
			step.setDirection(Direction.SOUTH.getDirection());
		}else if(parentStep.getY() < roadStep.getY()) {	//向上移动
			step.setDirection(Direction.NORTH.getDirection());
		}else {
			logger.error("出现无法解析成Step的路，检查！！！！！！！！！！！！！！！！！");
		}
		
		return step;
	}
	
	
	/**
	 * AI是否造成了伤害
	 * @param aiTank
	 * @return 0 无伤害，1伤害，2死亡
	 */
	private int aiHurt(AiTank aiTank) {
		int rValue = 0;
		Client c = ClientContainer.getInstance().getClient(aiTank.getAiTarget());
		if(c != null) {
			double distance = MapHelper.getDistanceBetweenTwoPoint(aiTank.getX(), aiTank.getY(), c.getX(), c.getY());
			if(distance <= GameStatus.AI_HURT_DISTANCE.getValue()) {
				boolean isAlive = c.beFire();
				rValue = 1;
				if(!isAlive) { //挂了,待完善					 
					 /** 推送死亡信息 **/
					 int dieSessionId = aiTank.getAiTarget();
					 ResAiKillMessage res = new ResAiKillMessage();
					 res.setDisSessionId(dieSessionId); 
					 res.setInfo(PromptInfo.AI_DIE.getPmt());
					 
					 for(Client client : aiTank.getMapGround().getClientInMap().values()){
						 client.pushMessage(res);
					 }
					 //清理战斗
					 MapManager.getInstance().removeMap(aiTank.getMapGround().getMapId());
					 rValue = 2;
				}
				
			}
		}
		return rValue;
	}
	
	/**
	 * 造成AI伤害后推送信息
	 * @param ai
	 */
	private void aiHurtPushMessage(AiTank ai) {
			/** 推送攻击 **/			
			ResAiHurtMessage res = new ResAiHurtMessage();
			res.setSessionId(ai.getAiTarget());
			res.setInfo(PromptInfo.AI_HURT.getPmt());
			
			for(Client c : ai.getMapGround().getClientInMap().values()) {
				c.pushMessage(res);
			}
			logger.error("推送ai伤害完毕");
	}
}
