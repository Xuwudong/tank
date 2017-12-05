package cn.senninha.game.map.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.senninha.game.map.Grid;
import cn.senninha.game.map.GridStatus;
import cn.senninha.game.map.manager.MapHelper;

public class AStarUtil {

	public static void main(String[] args) {
		/**
		 * 两点间距离计算公式测试 Grid grid0 = new Grid(); Grid grid1 = new Grid();
		 * grid0.setX((byte)3); grid1.setY((byte)4); double distance =
		 * distanceBetweenTwoPoint(grid0, grid1); System.out.println(distance);
		 **/

		/**
		 * 测试某个点周围的的格子距离
		List<Grid> list = MapHelper.generateGridRandom();
		List<TemFindRoad> tmp = caculateRoad(list, list.get(53), list.get(53), list.get(25), MapHelper.WIDTH_GRIDS,
				MapHelper.HEIGHT_GRIDS);
		for (TemFindRoad t : tmp) {
			System.out.println(t.toString());
		}
				 **/	
		
		/** 寻路测试 **/
		List<Grid> grids = MapHelper.generateGridRandom();
		AStarNode head = findRoad(grids, grids.get(0), grids.get(25), MapHelper.WIDTH_GRIDS, MapHelper.HEIGHT_GRIDS);
		System.out.println(head);

	}

	/**
	 * 
	 * @param grids
	 * @param source
	 * @param target
	 * @param width
	 *            横向的格子数
	 * @param height
	 *            竖向的格子数
	 * @return
	 */
	public static AStarNode findRoad(List<Grid> grids, Grid source, Grid target, int width, int height) {
		/** 头节点 closeList **/
		AStarNode head = new AStarNode();
		
		head.setValue(source);
		/** 当前的节点 **/
		AStarNode cur = head;

		Map<Grid, Grid> openList = new HashMap<>();	//待放入的
		Map<Grid, Grid> closeList = new HashMap<>();
		closeList.put(source, source);
		
		while (cur.getValue() != target) {
			List<TemFindRoad> tmp = caculateRoad(grids, source, cur.getValue(), target, width, height);
			if(tmp == null || tmp.size() == 0) {	//为空的话，说明根本就寻不到路，直接gg
				return null;
			}
			
			for(int i = 0 ; i < tmp.size() ; i++) {
				
				if(closeList.get(.getGrid()) != null) {
					
				}
			}
			
			TemFindRoad temFindRoad = tmp.remove(0);
			AStarNode aSTem = new AStarNode(temFindRoad.getGrid(), temFindRoad.getgValue());
			if(openList.get(temFindRoad) != null) { //在openlist里的话，要进一步进行处理
				if(cur.getgDistance() < aSTem.getgDistance()) {//小于的话，干掉cur
					AStarNode before = cur.getBefore();
					before.setNext(aSTem);
					aSTem.setBefore(before);
					cur.setBefore(null);//去除原来的cur对前面的引用
					
					cur = aSTem;
				}
			} else {
				cur.setNext(aSTem);
				aSTem.setBefore(cur);
				cur = aSTem;
			}
			
			/** 把剩下的全部加进openList里面 **/
			for(TemFindRoad t : tmp) {
				openList.put(t.getGrid(), t.getGrid());
			}
		}
		return head;
	}

	/**
	 * 计算current到target点四个方向的距离并排序，去除了不存在的点
	 * 
	 * @param current
	 * @param target
	 * @return
	 */
	private static List<TemFindRoad> caculateRoad(List<Grid> grids, Grid startGrid, Grid current, Grid target,
			int width, int height) {
		List<TemFindRoad> rValue = new LinkedList<>();

		/** 上 **/
		if (current.getY() - 1 >= 0) { // 不越界
			Grid next = grids.get(((current.getY() - 1) * width) + current.getX());
			caculateFourOrientionRoad(rValue, grids, startGrid, current, target, next);
		}
		/** 下 **/
		if (current.getY() + 1 < height) {
			Grid next = grids.get(((current.getY() + 1) * width) + current.getX());
			caculateFourOrientionRoad(rValue, grids, startGrid, current, target, next);
		}
		/** 左 **/
		if (current.getX() - 1 >= 0) {
			Grid next = grids.get(((current.getY()) * width) + current.getX() - 1);
			caculateFourOrientionRoad(rValue, grids, startGrid, current, target, next);
		}
		/** 右 **/
		if (current.getX() + 1 < width) {
			Grid next = grids.get(((current.getY()) * width) + current.getX() + 1);
			caculateFourOrientionRoad(rValue, grids, startGrid, current, target, next);
		}

		Collections.sort(rValue);

		return rValue;
	}

	/**
	 * 是否能走，并计算
	 * 
	 * @param rValue
	 * @param grids
	 * @param startGrid
	 * @param current
	 * @param target
	 * @param width
	 * @param height
	 */
	private static void caculateFourOrientionRoad(List<TemFindRoad> rValue, List<Grid> grids, Grid startGrid,
			Grid current, Grid target, Grid next) {
		if (next.getStatus() == GridStatus.CAN_RUN.getStatus()) { // 可走动
			double hValue = distanceBetweenTwoPoint(next, target);
			double gValue = distanceBetweenTwoPoint(startGrid, next);
			TemFindRoad tem = new TemFindRoad(next, gValue, hValue);
			rValue.add(tem);
		}
	}

	/**
	 * 计算两点间的距离，暂时使用格子计算而不是像素点计算
	 * 
	 * @param grid0
	 * @param grid1
	 * @return
	 */
	public static double distanceBetweenTwoPoint(Grid grid0, Grid grid1) {
		double distance = 0;
		distance = Math.pow(grid0.getX() - grid1.getX(), 2) + Math.pow(grid0.getPixelY() - grid1.getY(), 2);
		distance = Math.sqrt(distance);
		return distance;
	}

}

class TemFindRoad implements Comparable<TemFindRoad> {
	private Grid grid;
	private double gValue;
	private double hValue;
	
	

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public double getgValue() {
		return gValue;
	}

	public void setgValue(double gValue) {
		this.gValue = gValue;
	}

	public double gethValue() {
		return hValue;
	}

	public void sethValue(double hValue) {
		this.hValue = hValue;
	}

	public TemFindRoad(Grid grid, double gValue, double hValue) {
		super();
		this.grid = grid;
		this.gValue = gValue;
		this.hValue = hValue;
	}

	@Override
	public String toString() {
		return "TemFindRoad [grid=" + grid + ", gValue=" + gValue + ", hValue=" + hValue + "]";
	}

	@Override
	public int compareTo(TemFindRoad o) {
		if (o.gValue + o.hValue > this.gValue + this.hValue) {
			return -1;
		} else {
			return 1;
		}
	}
}
