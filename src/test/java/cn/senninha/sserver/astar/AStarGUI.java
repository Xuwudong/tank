package cn.senninha.sserver.astar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import cn.senninha.game.map.Grid;
import cn.senninha.game.map.GridStatus;
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
	private static final String SAVE_RESULT = "保存";
	private static final String LOAD_RESOURCE = "载入资源";
	private static final String RESOURCE = "sennninha";
	private static final String TMP = "/home/senninha/tmp/";

	/** 0,等待阻挡，1,等待设置起点，2等待设置终点,4, 正在寻路中 **/
	private int status = -1;

	private static String[] title = { "请点击设置阻挡，设置完毕后点击其他按钮继续", "设置起点，然后按其他按钮继续", "设置终点，然后点击其他按钮继续", "正在寻路中",
			"请先选择配置何种信息", "保存成功", "载入成功", "载入失败" };

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
		JButton saveResult = new JButton(SAVE_RESULT);
		JButton loadResult = new JButton(LOAD_RESOURCE);

		this.add(setBlocks);
		this.add(startFindRoad);
		this.add(startPoint);
		this.add(endPoint);
		this.add(saveResult);
		this.add(loadResult);

		setBlocks.addActionListener(this);
		startFindRoad.addActionListener(this);
		startPoint.addActionListener(this);
		endPoint.addActionListener(this);
		saveResult.addActionListener(this);
		loadResult.addActionListener(this);

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
				break;
			case SAVE_RESULT:
				writeToMap(grids);
				updateTitle(title[5]);
				break;
			case LOAD_RESOURCE:
				JFileChooser jf = new JFileChooser();
				jf.setCurrentDirectory(new File(TMP));
				jf.addChoosableFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "选择地图文件";
					}

					@Override
					public boolean accept(File f) {
						if (f.isDirectory() || f.getName().endsWith(RESOURCE)) {
							return true;
						}
						return false;
					}
				});
				jf.showOpenDialog(this);// 显示打开的文件对话框

				File file = jf.getSelectedFile();// 使用文件类获取选择器选择的文件
				if(file == null) {
					return;
				}
				boolean flag = this.readFromFile(file, grids);
				if (flag)
					updateTitle(title[6]);
				else
					updateTitle(title[7]);
				break;
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
	private void startFindRoad() {
		GridEntity start, end;
		start = findEntity(startColor);
		if (start == null) {
			updateTitle("请设置起点");
			return;
		}
		end = findEntity(endColor);
		if (end == null) {
			updateTitle("请设置终点");
			return;
		}

		clearColor(Color.white);

		long begin = System.currentTimeMillis();
		ASNode head = ASUtil.aStar(grids, start, end, width, height);
		long stop = System.currentTimeMillis();

		if (head == null) {
			updateTitle("无法找到路径");
			return;
		}
		while (head != null) {
			GridEntity g = (GridEntity) head.getValue();
			if (g.getColor().equals(Color.WHITE)) {
				g.setColor(road);
			}
			head = head.getParent();
		}
		updateTitle("驯鹿成功:" + (stop - begin) + "ms");
	}

	private GridEntity findEntity(Color target) {
		for (Grid g : grids) {
			if (((GridEntity) g).getColor().equals(target)) {
				return (GridEntity) g;
			}
		}
		return null;
	}

	private void clearColor(Color target) {
		for (Grid g : grids) {
			if (((GridEntity) g).getColor().equals(road)) {
				((GridEntity) g).setColor(target);
			}
		}
	}

	public static String writeToMap(List<Grid> list) {
		File directory = new File(TMP);
		if(!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(TMP + new Date().toString() + "." + RESOURCE);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			StringBuilder sb = new StringBuilder();
			sb.append(list.size());

			for (Grid grid : list) {
				GridEntity ge = (GridEntity) grid;
				Color c = ge.getColor();
				sb.append(",");
				sb.append(c.getRed());
				sb.append(",");
				sb.append(c.getGreen());
				sb.append(",");
				sb.append(c.getBlue());
			}
			byte[] b = sb.toString().getBytes("utf-8");
			fos.write(b, 0, b.length);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file.getName();
	}

	public boolean readFromFile(File file, List<Grid> list) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] b = new byte[1024];
			int len = fis.read(b);
			StringBuilder sb = new StringBuilder();
			while (len != -1) {
				sb.append(new String(b, 0, len, "utf-8"));
				len = fis.read(b);
			}

			// 解析
			String[] resource = sb.toString().split(",");

			int size = Integer.parseInt(resource[0]);
			
			for (int i = 1, j = 0; i <= size * 3; j++) {
				list.get(j).setStatus((byte)0);
				((GridEntity)list.get((j))).setColor(Color.white);
				Color c = new Color(Integer.parseInt(resource[i++]), Integer.parseInt(resource[i++]),
						Integer.parseInt(resource[i++]));
				((GridEntity) (list.get(j))).setColor(c);
				if(c.equals(Color.RED)) {
					list.get(j).setStatus(GridStatus.CAN_NOT_SHOT.getStatus());
				}else if(c.equals(Color.white) || c.equals(Color.blue) || c.equals(Color.YELLOW)) {
					list.get(j).setStatus(GridStatus.CAN_RUN.getStatus());
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;

	}

}
