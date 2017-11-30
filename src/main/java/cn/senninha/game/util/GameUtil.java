package cn.senninha.game.util;

/**
 * 游戏工具类
 * @author senninha
 *
 */
public class GameUtil {
	private static int ID = 10086;
	/**
	 * 生成id，主要是用来标识子弹和客户端的。。
	 * @return
	 */
	public static int generateIntegerId(){
		return ID++;
	}
}
