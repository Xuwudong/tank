package cn.senninha.sserver.astar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import cn.senninha.game.map.Grid;
import cn.senninha.game.map.manager.MapHelper;
import cn.senninha.game.map.util.ASNode;
import cn.senninha.game.map.util.ASUtil;

/**
 * A星驯鹿gui
 * 
 * @author senninha on 2017年12月10日
 *
 */
public class AStarGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = -381368157612204992L;
	private List<Grid> grids = null;
	private int width;
	private int height;

	private static final String START_POINT = "起点";
	private static final String END_POINT = "终点";
	private static final String SET_BLOCKS = "阻挡";
	private static final String START_FIND_ROADS = "驯鹿";

	/** 0,等待阻挡，1,等待设置起点，2等待设置终点,4, 正在寻路中 **/
	private int status = -1;

	private static String[] title = { "请点击设置阻挡，设置完毕后点击其他按钮继续", "设置起点，然后按其他按钮继续", "设置终点，然后点击其他按钮继续", "正在寻路中",
			"请先选择配置何种信息" };

	private Color blockColor = Color.RED;
	private Color startColor = Color.BLUE;
	private Color endColor = Color.ORANGE;
	private Color road = Color.GREEN;

	public static void main(String[] args) {
		AStarGUI aStarGUI = new AStarGUI(20, 15);
		aStarGUI.setVisible(true);
	}

	public AStarGUI(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		init();

	}

	private void init() {

		/** 添加阻挡等信息 **/
		grids = new ArrayList<>(width * height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSizeInfo = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSizeInfo);
		setLayout(new GridLayout(0, MapHelper.WIDTH_GRIDS));
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				JButton but = new JButton();
				but.setActionCommand((i * width + j) + ")");
				but.addActionListener(this);
				GridEntity g = new GridEntity();
				g.setX((byte) j);
				g.setY((byte) i);
				g.setBut(but);
				g.setColor(Color.white);
				grids.add(g);

				getContentPane().add(but);
			}
		}
		this.setVisible(true);

		/** 设置阻挡，设置起点，设置终点，开始寻路 **/
		JButton setBlocks = new JButton(SET_BLOCKS);
		JButton startFindRoad = new JButton(START_FIND_ROADS);
		JButton startPoint = new JButton(START_POINT);
		JButton endPoint = new JButton(END_POINT);

		this.add(setBlocks);
		this.add(startFindRoad);
		this.add(startPoint);
		this.add(endPoint);

		setBlocks.addActionListener(this);
		startFindRoad.addActionListener(this);
		startPoint.addActionListener(this);
		endPoint.addActionListener(this);

		this.setTitle("请点击阻挡设置阻挡");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.contains(")")) {// 点击的是设置阻挡等待
			int index = Integer.parseInt(command.substring(0, command.length() - 1));
			switch (status) {
			case 1:
				clearOtherColor(startColor, Color.WHITE);
				((GridEntity) grids.get(index)).setColor(startColor);
				break;
			case 2:
				clearOtherColor(endColor, Color.WHITE);
				((GridEntity) grids.get(index)).setColor(endColor);
				break;
			case 0:
				((GridEntity) grids.get(index)).setColor(blockColor);
				break;
			default:
				updateTitle(title[4]);
			}
		} else {// 监听下面的按钮
			switch (command) {
			case START_POINT:
				status = 1;
				updateTitle(title[1]);
				break;
			case END_POINT:
				status = 2;
				updateTitle(title[2]);
				break;
			case SET_BLOCKS:
				status = 0;
				updateTitle(title[0]);
				break;
			case START_FIND_ROADS:
				status = 3;
				updateTitle(title[3]);
				startFindRoad();
			}
		}
	}

	private void clearOtherColor(Color color, Color newColor) {
		for (Grid g : grids) {
			if (((GridEntity) g).getColor().equals(color)) {
				((GridEntity) g).setColor(newColor);
			}
		}
	}

	/**
	 * 更新提示信息的
	 * 
	 * @param title
	 */
	private void updateTitle(String title) {
		this.setTitle(title);
		;
	}
	
	/**
	 * 开始驯鹿
	 */
	private void startFindRoad(){
		GridEntity start, end;
		start = findEntity(startColor);
		if(start == null){
			updateTitle("请设置起点");
			return;
		}
		end = findEntity(endColor);
		if(end == null){
			updateTitle("请设置终点");
			return;
		}
		
		clearColor(Color.white);
		
		long begin = System.currentTimeMillis();
		ASNode head = ASUtil.aStar(grids, start, end, width, height);
		long stop = System.currentTimeMillis();
		
		if(head == null){
			updateTitle("无法找到路径");
			return;
		}
		while(head != null){
			GridEntity g = (GridEntity) head.getValue();
			g.setColor(road);
			head = head.getParent();
		}
		updateTitle("驯鹿成功:" + (stop - begin) + "ms");
	}
	
	private GridEntity findEntity(Color target){
		for(Grid g : grids){
			if(((GridEntity) g).getColor().equals(target)){
				return (GridEntity) g;
			}
		}
		return null;
	}
	
	private void clearColor(Color target){
		for(Grid g : grids){
			if(((GridEntity) g).getColor().equals(road)){
				((GridEntity) g).setColor(target);
			}
		}
	}

}
