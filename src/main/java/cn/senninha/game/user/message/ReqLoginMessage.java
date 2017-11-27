package cn.senninha.game.user.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.LOGIN_REQ)
public class ReqLoginMessage extends BaseMessage {
	private int sessionId;
	private String username;
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "ResLoginMessage [sessionId=" + sessionId + ", username="
				+ username + "]";
	}
}
