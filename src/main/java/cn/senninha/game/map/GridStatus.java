package cn.senninha.game.map;

/**
 * 格子的状态
 * @author senninha
 *
 */
public enum GridStatus {
	/** 可走 **/
	CAN_RUN(0),
	
	/** 可以击穿 **/
	CAN_SHOT(1),
	
	/**不可击穿 **/
	CAN_NOT_SHOT(2);
	
	

	private int status;	
	private GridStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
}
