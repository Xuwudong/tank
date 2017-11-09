package cn.senninha.sserver.message;

import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

@Message(cmd = 7)
public class HelloMessage extends BaseMessage {
	private int a;
	private int b;
	public int getA() {
		return a;
	}
	public int getB() {
		return b;
	}
	public void setA(int a) {
		this.a = a;
	}
	public void setB(int b) {
		this.b = b;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HelloMessage [a=");
		builder.append(a);
		builder.append(", b=");
		builder.append(b);
		builder.append("]");
		return builder.toString();
	}
	
}
