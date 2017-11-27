package cn.senninha.game.user.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.HEART_REQ)
public class ReqHeartbeatMessge extends BaseMessage {
	private long time;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "ReqHeartbeatMessge [time=" + time + "]";
	}

}
