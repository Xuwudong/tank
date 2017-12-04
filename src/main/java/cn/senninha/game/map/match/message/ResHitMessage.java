package cn.senninha.game.map.match.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.RES_HIT_RESULT)
public class ResHitMessage extends BaseMessage{
	private int hitFrom;
	private int hitTo;
	private int remainHp;
	@Override
	public String toString() {
		return "ResHitMessage [hitFrom=" + hitFrom + ", hitTo=" + hitTo + ", remainHp=" + remainHp + "]";
	}
	public ResHitMessage(int hitFrom, int hitTo, int remainHp) {
		super();
		this.hitFrom = hitFrom;
		this.hitTo = hitTo;
		this.remainHp = remainHp;
	}
	public int getHitFrom() {
		return hitFrom;
	}
	public void setHitFrom(int hitFrom) {
		this.hitFrom = hitFrom;
	}
	public int getHitTo() {
		return hitTo;
	}
	public void setHitTo(int hitTo) {
		this.hitTo = hitTo;
	}
	public int getRemainHp() {
		return remainHp;
	}
	public void setRemainHp(int remainHp) {
		this.remainHp = remainHp;
	}
	public ResHitMessage() {
		super();
	}
	
	
}
