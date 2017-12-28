package cn.senninha.sserver;

public interface CmdConstant {
	/** req **/
	/** 登陆 **/
	public static final int LOGIN_REQ = 1001;			
	/** 心跳 **/
	public static final int HEART_REQ = 1002;		
	/** 请求走动 **/
	public static final int RUN_REQ   = 1003;
	/** 请求射击 **/
	public static final int REQ_SHELLS = 1004;
	/** 请求匹配 **/
	public static final int REQ_MATCH = 1005;
	
	
	/**------------------------------------------------------------------**/
	
	/** resp **/
	/** 登陆 **/
	public static final int LOGIN_RES = 2001;
	//  心跳 **/
	public static final int HEART_RES = 2002;	
	/** 发送地图信息 **/
	public static final int MAP_RESOURCE_RES = 2003;
	/** 走动结果 **/
	public static final int RUN_RES = 2004;
	/** 射击结果 **/
	public static final int RES_BULLET = 2005;
	/** 匹配等待 **/
	public static final int RES_MATCH = 2006;
	/** 战斗结果 **/
	public static final int RES_BATTLE_RESULT = 2007;
	/** 战斗过程的推送 **/
	public static final int RES_HIT_RESULT = 2008;
	/** Ai 伤害推送 **/
	public static final int RES_AI_HURT = 2009;
	/** 被AI干掉 **/
	public static final int RES_AI_DIE = 2010;
	/** 击中Ai **/
	public static final int RES_SHOT_AI = 2011;
	
	
	
	/** 内部封装cmd **/
	/** 格子 **/
	public static final int GRID = 3000;
}
