package cn.senninha.game.map.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.senninha.game.map.Direction;
import cn.senninha.game.map.Grid;
import cn.senninha.game.map.GridStatus;
import cn.senninha.game.map.MapGround;
import cn.senninha.game.map.Steps;
import cn.senninha.game.map.message.ResRunResultMessage;
import cn.senninha.sserver.client.Client;

/**
 * Map帮助类
 * 
 * @author senninha
 *
 */
public class MapHelper {

	public static final int WIDTH_GRIDS = 20; // x方向的格子数
	public static final int HEIGHT_GRIDS = 15;
	public static final int TOTAL_GRIDS = WIDTH_GRIDS * HEIGHT_GRIDS;

	public static final int PER_GRID_PIXEL = 40; // 每个格子的像素
	private static Random r = new Random();

	public static List<Grid> generateGridRandom() {
		List<Grid> list = new ArrayList<Grid>(TOTAL_GRIDS);
		for (int i = 0; i < TOTAL_GRIDS; i++) {
			if (i % 5 == 0) {
				list.add(new Grid((byte) (i % WIDTH_GRIDS), (byte) (i / WIDTH_GRIDS),
						(byte) (GridStatus.CAN_NOT_SHOT.getStatus())));
				if(i == 10 || i == 20 || i == 30 || i == 40 || i == 5){
					list.get(i).setStatus(GridStatus.CAN_RUN.getStatus());
				}
			} else {
				list.add(new Grid((byte) (i % WIDTH_GRIDS), (byte) (i / WIDTH_GRIDS),
						(byte) (GridStatus.CAN_RUN.getStatus())));
			}
		}
		return list;
	}

	/**
	 * 校验跑动
	 * 
	 * @param client
	 * @return 如果不能走动，返回false
	 */
	public static ResRunResultMessage validateRun(Client client) {
		Steps step = client.getHeadStepButNotRemove();
		
		if(step == null) {
			return null;
		}

		long current = System.currentTimeMillis();
		long intervel = current - step.getGenerateTime();

		int hasRun = (int) (intervel / 5 * client.getSpeed()); // 已经走过的像素
		hasRun = hasRun > step.getStep() ? step.getStep() : hasRun;	//如果太大取小的值
		int x = client.getX();
		int y = client.getY();

		byte status = step.getDirection();
		if (status == Direction.EAST.getDirection()) {
			x = x + hasRun;
		} else if (status == Direction.WEST.getDirection()) {
			x = x - hasRun;
		} else if (status == Direction.SOUTH.getDirection()) {
			y = y + hasRun;
		} else if (status == Direction.NORTH.getDirection()) {
			y = y - hasRun;
		}

		boolean canRun = canRun(client, x, y);
		
		if(canRun) {
			//去除已走动的距离
			hasRun = step.getStep() - hasRun;
			if(hasRun <= 0) {
				client.removeHeadSteps();
			}else {	//否则的话，不移除，继续走走走！！！
				step.setStep((byte)hasRun);
				step.setGenerateTime(current);
			}
			return new ResRunResultMessage(x, y, client.getSessionId(), status);
		}else {	//如果不能走动，直接返回false，并且移除这个走动
			client.removeHeadSteps();
			return null;
		}

	}

	/**
	 * 根据像素返回这个点所占据的格子下标，因为格子是list储存的。。
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static int convertPixelToGridIndex(int x, int y) {
		x = x / MapHelper.PER_GRID_PIXEL;
		y = y / MapHelper.PER_GRID_PIXEL;
		
		return y * MapHelper.WIDTH_GRIDS + x;
	}

	/**
	 * 判断是否可走并且直接更新进去地图，不能在未走的情况下调用这个方法
	 * @param client
	 * @param x
	 * @param y
	 * @return
	 */
	private static boolean canRun(Client client, int x, int y) {
		if(x < 0 || x >= WIDTH_GRIDS * PER_GRID_PIXEL
				|| y < 0 || y >= HEIGHT_GRIDS * PER_GRID_PIXEL) {//超过了格子，直接干掉,防止出现越界～
			return false;
		}
		return client.updateLocation(x, y);
	}
	
	/**
	 * 随机一个出生点
	 * @param ground
	 * @return
	 */
	public static int[] getRandomBorn(MapGround ground){
		for(Grid grid : ground.getBlocks()) {
			if(grid.getStatus() == GridStatus.CAN_RUN.getStatus()) {
				return new int[]{grid.getX(), grid.getY()};
			}
		}
		return null;
	}
}
