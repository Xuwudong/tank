package cn.senninha.game.map.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.senninha.game.GameStatus;
import cn.senninha.game.map.Direction;
import cn.senninha.game.map.Grid;
import cn.senninha.game.map.GridStatus;
import cn.senninha.game.map.MapGround;
import cn.senninha.game.map.Steps;
import cn.senninha.game.map.message.ReqShellsMessage;
import cn.senninha.game.map.message.ResRunResultMessage;
import cn.senninha.sserver.client.AiTank;
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
	public static final String name = MapHelper.class.getResource("/").toString() + "map37.resource";

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
		list.get(0).setStatus(GridStatus.CAN_RUN.getStatus()); //设置出生点为可走
		list.get(280).setStatus(GridStatus.CAN_RUN.getStatus());
		return list;
	}
	
	/**
	 * 从文件里获取地图资源
	 * @param file
	 * @return
	 */
	public static List<Grid> getMapFromMapFile(String fileName){
		fileName = fileName.substring(fileName.indexOf('/'));
		List<Grid> list = new ArrayList<>(MapHelper.TOTAL_GRIDS);
		File file = new File(fileName);
		if(!file.exists()) {
			System.err.println("找不到地图资源");
			System.exit(-1);
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] b = new byte[1024];
			int len = fis.read(b, 0, b.length);
			StringBuilder sb = new StringBuilder();
			while(len != -1) {
				sb.append(new String(b, 0, len, "utf-8"));
				len = fis.read(b, 0, b.length);
			}
			
			String resource = sb.toString();
			for(int i = 0 ; i < resource.length() ; i++) {
				Grid e = new Grid((byte) (i % WIDTH_GRIDS), (byte) (i / WIDTH_GRIDS), Byte.parseByte(resource.charAt(i) + ""));
				list.add(e);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
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

		int hasRun = (int) (intervel / GameStatus.GAME_PER_MILLTIME.getValue() * client.getSpeed()); // 已经走过的像素
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
			ResRunResultMessage res = new ResRunResultMessage(x, y, client.getSessionId(), step.getDirection());
			if(client instanceof AiTank) {
				res.setIsAI((byte)1); //AI坦克标志
			}
			client.setDirection(res.getDirection());
			return res;
		}else {	//如果不能走动，移除全部走动
			client.clearAllSteps();
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
		if(x < PER_GRID_PIXEL / 2 || x > WIDTH_GRIDS * PER_GRID_PIXEL - PER_GRID_PIXEL / 2
				|| y < PER_GRID_PIXEL / 2 || y > HEIGHT_GRIDS * PER_GRID_PIXEL - PER_GRID_PIXEL / 2) {//超过了格子，直接干掉,防止出现越界～
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
	
	/**
	 * 计算两点之间的平方
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @return
	 */
	public static double getDistanceBetweenTwoPoint(int x0, int y0, int x1, int y1) {
		double xPow = Math.pow((x0 - x1), 2);
		double yPow = Math.pow((y0 - y1), 2);
		return Math.sqrt(xPow + yPow);
	}
	
	public static void main(String[] args) {
		int gridIndex = 20;
		System.out.println(convertPixelToGridIndex(convertGridIndexToPixel(gridIndex)[0], convertGridIndexToPixel(gridIndex)[1]));
	}
}
