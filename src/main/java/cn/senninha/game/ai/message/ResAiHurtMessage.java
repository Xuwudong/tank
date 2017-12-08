package cn.senninha.game.ai.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.RES_AI_HURT)
public class ResAiHurtMessage extends BaseMessage {
	private int sessionId;
	private String info;
	
	@Override
	public String toString() {
		return "ResAiHurtMessage [sessionId=" + sessionId + ", info=" + info + "]";
	}
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
}
