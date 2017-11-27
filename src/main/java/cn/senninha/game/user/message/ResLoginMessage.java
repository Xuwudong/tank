package cn.senninha.game.user.message;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = CmdConstant.LOGIN_RES)
public class ResLoginMessage extends BaseMessage {
	private byte status;
	private String info;
	
	public static ResLoginMessage valueOf(byte status) {
		ResLoginMessage m = new ResLoginMessage();
		if(status == 1) {
			m.setStatus((byte)1);
			m.setInfo("登陆成功");
		}else {
			m.setInfo("登陆失败");
		}
		return m;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "ResLoginMessage [status=" + status + ", info=" + info + "]";
	}

}
