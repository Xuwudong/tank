package cn.senninha.sserver.client;

/**
 * 客户端
 * @author senninha on 2017年11月8日
 *
 */
public class Client {
	private int sessionId;
	private String name;
	public int getSessionId() {
		return sessionId;
	}
	public String getName() {
		return name;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Client(int sessionId, String name) {
		super();
		this.sessionId = sessionId;
		this.name = name;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Client [sessionId=");
		builder.append(sessionId);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
