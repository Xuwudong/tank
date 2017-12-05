package cn.senninha.game.map.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.senninha.game.map.Direction;
import cn.senninha.game.map.Grid;
import cn.senninha.game.map.GridStatus;
import cn.senninha.game.map.MapGround;
import cn.senninha.game.map.Steps;
import cn.senninha.game.map.message.ReqShellsMessage;
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
	
	private static final Random r = new Random();

	public static List<Grid> generateGridRandom() {
		List<Grid> list = new ArrayList<Grid>(TOTAL_GRIDS);
		for (int i = 0; i < TOTAL_GRIDS; i++) {
			if (i % 5 == 0) {
				list.add(new Grid((byte) (i % WIDTH_GRIDS), (byte) (i / WIDTH_GRIDS),
						(byte) (GridStatus.CAN_NOT_SHOT.getStatus())));
				if(r.nextInt(2) == 0){
					list.get(i).setStatus(GridStatus.CAN_RUN.getStatus());
				}
			} else {
				list.add(new Grid((byte) (i % WIDTH_GRIDS), (byte) (i / WIDTH_GRIDS),
						(byte) (GridStatus.CAN_RUN.getStatus())));
			}
//			list.get(i).setStatus(GridStatus.CAN_RUN.getStatus());
		}
		list.get(0).setStatus(GridStatus.CAN_RUN.getStatus()); //设置出生点
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
		
		int[] xy = correctXY(x, y);
		x = xy[0]; y = xy[1];
		
		return y * MapHelper.WIDTH_GRIDS + x;
	}
	
	public static int[] convertGridIndexToPixel(int gridIndex) {
		int[] xy = new int[2];
		xy[0] = gridIndex % WIDTH_GRIDS * PER_GRID_PIXEL;
		xy[1] = gridIndex / WIDTH_GRIDS * PER_GRID_PIXEL;
		return xy;
	}
	
	/**
	 * 修正xy，超过最大像素边界的问题，回复成不越界的值
	 * @param x
	 * @param y
	 * @return
	 */
	public static int[] correctXY(int x, int y){		
		if(x < 0) {
			x = 0;
		}else if(x >= PER_GRID_PIXEL * WIDTH_GRIDS) {
			x = PER_GRID_PIXEL * WIDTH_GRIDS - 1;
		}
		
		if(y < 0) {
			y = 0;
		}else if(y >= PER_GRID_PIXEL * HEIGHT_GRIDS) {
			y = PER_GRID_PIXEL * HEIGHT_GRIDS - 1;
		}
		
		return new int[]{x, y};
	}
	
	/**
	 * x,y值是否越界
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean needCorrect(int x ,int y) {
		if(x < 0 || y < 0 || x >= PER_GRID_PIXEL * WIDTH_GRIDS || y >= PER_GRID_PIXEL * HEIGHT_GRIDS) {
			return true;
		}
		return false;
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
	
	/**
	 * 判断是否可以射击
	 * @param client
	 * @return
	 */
	public static boolean validateCanFire(Client client){
		long current = System.currentTimeMillis();
		long interval = current - client.getFireTime();
		if(interval > client.getFireIntervel()){
			//重新设置开火时间
			client.setFireTime(current);
			return true;
		}
		return false;
	}
	
	/**
	 * 校正射击的源点，直接把它弄到下一个位置
	 * @param req
	 * @return 返回false的话，说明这次射击直接废弃即可
	 */
	public static boolean corrcetFireSource(ReqShellsMessage req) {
		int x = req.getX();
		int y = req.getY();
		int direction = req.getDirection();
		int index = convertPixelToGridIndex(x, y);
		
		if(direction == Direction.NORTH.getDirection()) {//北边
			if(index <= WIDTH_GRIDS) {
				return false;
			}
			int leave = y % PER_GRID_PIXEL;
			y = y - leave - 1;
		}else if(direction == Direction.EAST.getDirection()) {//右边
			if(index % WIDTH_GRIDS == WIDTH_GRIDS - 1) {
				return false;
			}
			int leave = x % PER_GRID_PIXEL;
			x = x + PER_GRID_PIXEL - leave;
		}else if(direction == Direction.SOUTH.getDirection()) {//下边
			if(index >= WIDTH_GRIDS * (HEIGHT_GRIDS - 1)) {
				return false;
			}
			int leave = y % PER_GRID_PIXEL;
			y = y + PER_GRID_PIXEL - leave;
		}else if(direction == Direction.WEST.getDirection()) {//西边
			if(index % WIDTH_GRIDS == 0) {
				return false;
			}
			int leave = x % PER_GRID_PIXEL;
			x = x - leave - 1;
		}
		req.setX(x);
		req.setY(y);
		return true;
	}
	
	public static void main(String[] args) {
		int gridIndex = 20;
		System.out.println(convertPixelToGridIndex(convertGridIndexToPixel(gridIndex)[0], convertGridIndexToPixel(gridIndex)[1]));
	}
}
