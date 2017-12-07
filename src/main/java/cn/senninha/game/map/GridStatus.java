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
	HAS_PLAYER((byte)3),
	
	/**坦克，自己 向上 **/
	SELF_TANK_UP((byte)4),
	
	/** 坦克，自己，向右**/
	SELF_TANK_RIGHT((byte)5),
	
	/**坦克，自己 向下**/
	SELF_TANK_DOWN((byte)6),
	
	/**坦克，自己，向左 **/
	SELF_TANK_LEFT((byte)7),
	
	/**坦克，别人 向上 **/
	OTHER_TANK_UP((byte)8),
	
	/** 坦克，别人，向右**/
	OTHER_TANK_RIGHT((byte)9),
	
	/**坦克，别人 向下**/
	OTHER_TANK_DOWN((byte)10),
	
	/**坦克，别人，向左 **/
	OTHER_TANK_LEFT((byte)11),
	
	/** 炮弹 **/
	SHELLS((byte)12),
	
	/** 炸弹爆炸 **/
	BOOM0((byte) 13),
	
	/** AI向上 **/
	AI_UP((byte) 14),
	
	/** AI向左 **/
	AI_RIGHT((byte) 15),

	/** AI向下 **/
	AI_DOWN((byte) 16),
	
	/** AI向左 **/
	AI_LEFT((byte) 17),
	;
	
	

	private byte status;	
	private GridStatus(byte status) {
		this.status = status;
	}
	
	public byte getStatus() {
		return status;
	}
}
