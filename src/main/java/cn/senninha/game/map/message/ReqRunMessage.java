package cn.senninha.game.map.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.RUN_REQ)
public class ReqRunMessage extends BaseMessage {
	private byte x;
	private byte y;
	private byte gridStep;
	private byte direction;
	
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
	 * 步子的跨越的格子数
	 * @return
	 */
	public byte getGridStep() {
		return gridStep;
	}

	public void setGridStep(byte gridStep) {
		this.gridStep = gridStep;
	}

	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "ReqRunMessage [x=" + x + ", y=" + y + ", gridStep=" + gridStep + ", direction=" + direction + "]";
	}
	
	
}
