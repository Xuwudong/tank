package cn.senninha.game.map;

/**
 * 方向枚举
 * @author senninha
 *
 */
public enum Direction {
	/** 北，东，南，西 **/
	NORTH((byte)0),
	EAST((byte)1),
	SOUTH((byte)2),
	WEST((byte)3),
	
	/** 单次请求最大步长 **/
	MAX_GRID_STEP((byte)2);
	
	
	
	private byte direction;
	private Direction(byte direction) {
		this.direction = direction;
	}
	
	public byte getDirection() {
		return this.direction;
	}
}
