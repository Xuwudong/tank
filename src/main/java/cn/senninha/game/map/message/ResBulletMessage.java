package cn.senninha.game.map.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.RES_BULLET)
public class ResBulletMessage extends BaseMessage {
	private int id;
	private byte status; //子弹的状态
	private int x;
	private int y;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	@Override
	public String toString() {
		return "ResBulletMessage [id=" + id + ", status=" + status + ", x=" + x + ", y=" + y + "]";
	}
	
	
}
