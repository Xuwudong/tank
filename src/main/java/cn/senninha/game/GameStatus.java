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
	
	/** 用户射击冷却时间 **/
	GAEM_BULLETS_SHOT_INTERVAL(1000),
	
	/** AI检测任务 **/
	GAME_AI_CHECK_INTERVAL(100),
	
	/** 通用子弹速度 / 10ms**/
	GAME_COMMON_BULLET_SPEED(10),
	
	/** 坦克速度 / 10ms **/
	GAME_COMMON_TANK_SPEED(2),
	
	/** 生命值 **/
	GAME_LIVE(10),
	
	/** AI每一步的步长 **/
	GAME_AI_PER_STEP(4),
	
	/** AI的速度 /GAME_PER_MILLTIME **/
	GAME_AI_SPEED(1),
	
	/** 上面的速度是 /多少毫秒 **/
	GAME_PER_MILLTIME(10),
	
	/** AI造成伤害的距离 **/
	AI_HURT_DISTANCE(60),
	
	/** AI造成伤害后冷却的时间 ms**/
	AI_HURT_COOL_DOWN(5000),
	
	
	;
	
	
	private int value;
	private GameStatus(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
