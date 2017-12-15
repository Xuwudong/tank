package cn.senninha.sserver.client;

import java.util.List;

import cn.senninha.game.GameStatus;
import cn.senninha.game.map.Grid;
import cn.senninha.game.map.Steps;
import io.netty.channel.ChannelHandlerContext;

/**
 * AI-Tank
 * 
 * @author senninha
 *
 */
public class AiTank extends Client {
	private boolean isAi;
	private int bornX;
	private int bornY;
	private int aiTarget;
	private int lastTargetGridIndex;
	private long lastHrut;

	public AiTank(int sessionId, String name, ChannelHandlerContext ctx) {
		super(sessionId, name, ctx);
		this.lastTargetGridIndex = -1;
		isAi = true;
	}

	/**
	 * 是否是AI
	 * 
	 * @return
	 */
	public boolean isAi() {
		return isAi;
	}

	public void setAi(boolean isAi) {
		this.isAi = isAi;
	}

	/**
	 * 获取ai的攻击目标
	 * 
	 * @return
	 */
	public int getAiTarget() {
		return aiTarget;
	}

	public void setAiTarget(int aiTarget) {
		this.aiTarget = aiTarget;
	}

	/**
	 * 出生的位置
	 * 
	 * @return
	 */
	public int getBornX() {
		return bornX;
	}

	public void setBornX(int bornX) {
		this.bornX = bornX;
	}

	/**
	 * 出生的位置
	 * 
	 * @return
	 */
	public int getBornY() {
		return bornY;
	}

	public void setBornY(int bornY) {
		this.bornY = bornY;
	}


	/**
	 * 是否需要寻路，构造对象的时候，把上一次的GridIndex设置为-1
	 * @param curGridIndex
	 * @return
	 */
	public boolean needFindRoad(int curGridIndex) {
		return this.lastTargetGridIndex != curGridIndex;
	}

	/**
	 * 目标坦克上一次的位置，决定是否需要再次寻路
	 * @return
	 */
	public void setLastTargetGridIndex(int lastTargetGridIndex) {
		this.lastTargetGridIndex = lastTargetGridIndex;
	}
	
	/**
	 * 添加行走路程
	 * @param steps
	 */
	public void addSteps(List<Steps> steps) {
		for(Steps step : steps) {
			super.addSteps(step);
		}
	}
	
	@Override
	protected boolean halfValueCheck(List<Grid> grids, int x, int y) {
		return super.halfValueCheck(grids, x, y);
	}
	
	/**
	 * 冷却时间是否到达
	 * @return
	 */
	public boolean isCoolDown() {
		long cur = System.currentTimeMillis();
		if(cur - lastHrut >= GameStatus.AI_HURT_COOL_DOWN.getValue()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 设置开始冷却时间
	 */
	public void setCoolDown() {
		this.lastHrut = System.currentTimeMillis();
	}

}
