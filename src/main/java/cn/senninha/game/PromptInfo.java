package cn.senninha.game;

/**
 * 提示信息
 * @author senninha
 *
 */
public enum PromptInfo {
	/** 等待匹配中 **/
	WAIT_TO_MATCH("等待匹配中"),
	
	;
	private String info;
	private PromptInfo(String info) {
		this.info = info;
	}
	
	public String getPmt() {
		return info;
	}
}
