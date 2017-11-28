package cn.senninha.game.map;

import cn.senninha.game.map.message.ReqRunMessage;

/**
 * 走动的实体
 * @author senninha
 *
 */
public class Steps {
	private byte x;
	private byte y;
	private byte stepPixel;
	private byte direction;
	private long generateTime;
	
	public static Steps valueOf(ReqRunMessage m) {
		Steps steps = new Steps();
		steps.direction = m.getDirection();
		steps.stepPixel = m.getGridStep();
		steps.x = m.getX();
		steps.y = m.getY();
		steps.generateTime = System.currentTimeMillis();
		return steps;
	}
	@Override
	public String toString() {
		return "Steps [x=" + x + ", y=" + y + ", step=" + stepPixel + ", direction=" + direction + ", generateTime="
				+ generateTime + "]";
	}
	public byte getX() {
		return x;
	}
	public void setX(byte x) {
		this.x = x;
	}
	public byte getY() {
		return y;
	}
	public void setY(byte y) {
		this.y = y;
	}
	/**
	 * 这个是像素，像素，不是格子，不是格子！！！
	 * @return
	 */
	public byte getStep() {
		return stepPixel;
	}
	/**
	 * 像素像素，不是格子！！！
	 * @param step
	 */
	public void setStep(byte step) {
		this.stepPixel = step;
	}
	public byte getDirection() {
		return direction;
	}
	public void setDirection(byte direction) {
		this.direction = direction;
	}
	public long getGenerateTime() {
		return generateTime;
	}
	public void setGenerateTime(long generateTime) {
		this.generateTime = generateTime;
	}
}
