package cn.senninha.game.map;

/**
 * 格子的状态
 * @author senninha
 *
 */
public enum GridStatus {
	/** 可走 **/
	CAN_RUN((byte)0),
	
	/** 可以击穿 **/
	CAN_SHOT((byte)1),
	
	/**不可击穿 **/
	CAN_NOT_SHOT((byte)2),
	
	/** 有人在这个格子 **/
	HAS_PLAYER((byte)3);
	
	

	private byte status;	
	private GridStatus(byte status) {
		this.status = status;
	}
	
	public byte getStatus() {
		return status;
	}
}
