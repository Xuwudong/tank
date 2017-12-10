package cn.senninha.game.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.game.GameStatus;
import cn.senninha.game.PromptInfo;
import cn.senninha.game.ai.message.ResAiHurtMessage;
import cn.senninha.game.map.Direction;
import cn.senninha.game.map.Grid;
import cn.senninha.game.map.MapGround;
import cn.senninha.game.map.Steps;
import cn.senninha.game.map.manager.MapHelper;
import cn.senninha.game.map.manager.MapManager;
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

				boolean canHurt = ai.isCoolDown();
				
				if(!canHurt) {	//未冷却
					continue;
				}
				
				boolean isHrut = aiHurt(ai);
				if(isHrut) { //如果已经冷却并且造成了伤害
					ai.clearAllSteps();
					aiHurtPushMessage(ai);
					ai.setCoolDown();	 //重新设置冷却时间
					continue;
				}
				
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
	 * @return
	 */
	private boolean aiHurt(AiTank aiTank) {
		boolean rValue = false;
		Client c = ClientContainer.getInstance().getClient(aiTank.getAiTarget());
		if(c != null) {
			double distance = MapHelper.getDistanceBetweenTwoPoint(aiTank.getX(), aiTank.getY(), c.getX(), c.getY());
			if(distance <= GameStatus.AI_HURT_DISTANCE.getValue()) {
				boolean isAlive = c.beFire();
				if(!isAlive) { //挂了,待完善
					
				}
				rValue = true;
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
