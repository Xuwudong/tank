package cn.senninha.game;

/**
 * 游戏中的一些常量
 * @author senninha
 *
 */
public enum GameStatus {
	/** 跑步检测的间隔时间 **/
	GAME_RUN_CHECK_INTERVEL(100),
	
	/** 子弹检测的间隔时间 **/
	GAME_BULLETS_CHECK_INTERVEL(50),
	
	/** 通用子弹速度 / 10ms**/
	GAME_COMMON_BULLET_SPEED(5),
	
	/** 坦克速度 / 10ms **/
	GAME_COMMON_TANK_SPEED(1),
	
	/** 生命值 **/
	GAME_LIVE(10),
	
	
	;
	
	
	private int value;
	private GameStatus(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
