package cn.senninha.game.user.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.HEART_RES)
public class ResHeartbeatMessge extends BaseMessage {
	private long time;
	private long current;

	public  ResHeartbeatMessge(ReqHeartbeatMessge m) {
		this.time = m.getTime();
		this.current = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "ResHeartbeatMessge [time=" + time + ", current=" + current + "]";
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}
	
	

}
