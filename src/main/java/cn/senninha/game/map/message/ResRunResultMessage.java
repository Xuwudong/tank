package cn.senninha.game.map.message;


import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.RUN_RES)
public class ResRunResultMessage extends BaseMessage{
	private int x;
	private int y;
	private int direction;
	private int sessionId;
	

	public ResRunResultMessage() {
		super();
	}

	public ResRunResultMessage(int x, int y, int sessionId, int direction) {
		super();
		this.x = x;
		this.y = y;
		this.sessionId = sessionId;
		this.direction = direction;
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

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "ResRunResultMessage [x=" + x + ", y=" + y + ", direction=" + direction + ", sessionId=" + sessionId
				+ "]";
	}

}
