package cn.senninha.game.map;

import cn.senninha.sserver.CmdConstant;
import cn.senninha.sserver.lang.message.MessageWrapperAnnotation;

/**
 * 格子
 * @author senninha
 *
 */
@MessageWrapperAnnotation(cmd = CmdConstant.GRID)
public class Grid {
	private byte x;
	private byte y;
	private int pixelX;
	private int pixelY;
	private byte status;
	private int sessionId;
	public Grid(byte x, byte y, byte status) {
		super();
		this.x = x;
		this.y = y;
		this.status = status;
	}
	
	
	public Grid() {
		super();
	}


	@Override
	public String toString() {
		return "Grid [x=" + x + ", y=" + y + ", pixelX=" + pixelX + ", pixelY=" + pixelY + ", status=" + status
				+ ", sessionId=" + sessionId + "]";
	}
	public byte getX() {
		return x;
	}
	public void setX(byte x) {
		this.x = x;
	}
	public byte getY() {
		return y;
	}
	public void setY(byte y) {
		this.y = y;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}


	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}


	public int getPixelX() {
		return pixelX;
	}


	public void setPixelX(int pixelX) {
		this.pixelX = pixelX;
	}


	public int getPixelY() {
		return pixelY;
	}


	public void setPixelY(int pixelY) {
		this.pixelY = pixelY;
	}
	
}