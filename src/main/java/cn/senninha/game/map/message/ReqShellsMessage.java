package cn.senninha.game.map.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.client.AiTank;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.REQ_SHELLS)
public class ReqShellsMessage extends BaseMessage {
	private int sourceSessionId;
	private int x;
	private int y;
	private byte direction;
	
	
	public static ReqShellsMessage valueOf(AiTank tank) {
		ReqShellsMessage req = new ReqShellsMessage();
		req.setSourceSessionId(tank.getSessionId());
		req.setDirection((byte)tank.getDirection());
		req.setX(tank.getX());
		req.setY(tank.getY());
		return req;
	}
	public int getSourceSessionId() {
		return sourceSessionId;
	}
	public void setSourceSessionId(int sourceSessionId) {
		this.sourceSessionId = sourceSessionId;
	}
	/**
	 * 这个是像素点！！！
	 * @return
	 */
	public int getX() {
		return x;
	}
	/**
	 * 我也是像素点
	 * @param x 像素点
	 */
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public byte getDirection() {
		return direction;
	}
	public void setDirection(byte direction) {
		this.direction = direction;
	}
	@Override
	public String toString() {
		return "ReqShellsMessage [sourceSessionId=" + sourceSessionId + ", x=" + x + ", y=" + y + ", direction="
				+ direction + "]";
	}
	
	
}
