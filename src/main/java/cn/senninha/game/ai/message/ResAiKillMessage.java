package cn.senninha.game.ai.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.RES_AI_DIE)
public class ResAiKillMessage extends BaseMessage {
	private int disSessionId;
	private String info;
	public int getDisSessionId() {
		return disSessionId;
	}
	public void setDisSessionId(int disSessionId) {
		this.disSessionId = disSessionId;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	@Override
	public String toString() {
		return "ResAiKillMessage [disSessionId=" + disSessionId + ", info=" + info + "]";
	}
	
}
