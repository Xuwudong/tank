package cn.senninha.game.map;

/**
 * 子弹实体
 * @author senninha
 *
 */
public class BulletsObject {
	private int x;
	private int y;
	private byte direction;
	private int id;
	private int sourceSessionId;
	private long lastCheckTime;
	private int speed;
	private MapGround mapGround;
	private int paintTime;
	
	/**
	 * 经过绘制的次数
	 * @return
	 */
	public int getPaintTime() {
		return paintTime;
	}
	/**
	 * 经过绘制的次数
	 * @param paintTime
	 */
	public void setPaintTime(int paintTime) {
		this.paintTime = paintTime;
	}
	public MapGround getMapGround() {
		return mapGround;
	}
	public void setMapGround(MapGround mapGround) {
		this.mapGround = mapGround;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSourceSessionId() {
		return sourceSessionId;
	}
	public void setSourceSessionId(int sourceSessionId) {
		this.sourceSessionId = sourceSessionId;
	}
	public byte getDirection() {
		return direction;
	}
	public void setDirection(byte direction) {
		this.direction = direction;
	}
	public long getLastCheckTime() {
		return lastCheckTime;
	}
	public void setLastCheckTime(long lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	@Override
	public String toString() {
		return "BulletsObject [x=" + x + ", y=" + y + ", direction=" + direction + ", id=" + id + ", sourceSessionId="
				+ sourceSessionId + ", lastCheckTime=" + lastCheckTime + ", speed=" + speed + ", mapGround=" + mapGround
				+ ", paintTime=" + paintTime + "]";
	}
	
	
}
