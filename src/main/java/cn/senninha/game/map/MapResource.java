package cn.senninha.game.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapResource {
	public static final int WIDTH_GRIDS = 30;  //	x方向的格子数
	public static final int HEIGHT_GRIDS = 30;
	public static final int TOTAL_GRIDS = WIDTH_GRIDS * HEIGHT_GRIDS;
	private static Random r = new Random();
	public static List<Grid> generateGridRandom(){
		List<Grid> list = new ArrayList<Grid>(TOTAL_GRIDS);
		for(int i = 0; i < TOTAL_GRIDS; i++) {
			list.add(new Grid((byte)(i % WIDTH_GRIDS), (byte)(i / WIDTH_GRIDS), (byte)(r.nextInt(3))));
		}
		return list;
	}
	
	public static void main(String[] args) {
		List<Grid> lists = generateGridRandom();
		System.out.println(lists);
	}
}
