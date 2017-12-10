package cn.senninha.game;

/**
 * 提示信息
 * @author senninha
 *
 */
public enum PromptInfo {
	/** 等待匹配中 **/
	WAIT_TO_MATCH("等待匹配中"),
	
	AI_HURT("被警察坦克抓到，赶快逃离，生命值减1："),
	
	GAME_RULE("射击绿色的坦克，同时不能被蓝色的警察抓到，一旦被抓到，生命值-1"),
	
	AI_DIE("被ai击杀")
	
	;
	private String info;
	private PromptInfo(String info) {
		this.info = info;
	}
	
	public String getPmt() {
		return info;
	}
}
