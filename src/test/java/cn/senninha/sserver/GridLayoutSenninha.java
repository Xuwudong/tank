package cn.senninha.sserver;

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

import cn.senninha.game.map.GridStatus;
import cn.senninha.game.map.manager.MapHelper;

public class GridLayoutSenninha extends JFrame implements ActionListener {
	private List<MyButton> blocks = new ArrayList<>(MapHelper.TOTAL_GRIDS);
	/**
	 * 
	 */
	private static final long serialVersionUID = 7200897797286531175L;

	public GridLayoutSenninha() {
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSizeInfo = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSizeInfo);
		setLayout(new GridLayout(0, MapHelper.WIDTH_GRIDS));
		for (int i = 0; i < MapHelper.TOTAL_GRIDS; i++) {
			MyButton but = new MyButton();
			but.setActionCommand(i + "");
			but.addActionListener(this);
			blocks.add(but);
			getContentPane().add(but);

		}
		setTitle("需要添加阻挡的地方鼠标淡季变红：" + GridStatus.CAN_NOT_SHOT.getStatus());
		JButton but = new JButton("保存");
		getContentPane().add(but);
		but.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("保存")) {
			String name = MapGenerateUtil.writeToMap(blocks);
			if(name != null) {
				this.setTitle("10s后推出，保存成功:" + name);
				try {
					Thread.sleep(10000);
					System.exit(-1);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}else {
				this.setTitle("保存失败");
			}
		} else {
			int index = Integer.valueOf(command);
			MyButton mb = blocks.get(index);
			if (mb.isBlock()) {
				mb.setBackground(Color.WHITE);
				mb.setBlock(false);
			} else {
				mb.setBackground(Color.RED);
				mb.setBlock(true);
			}
		}
	}

	public static void main(String[] args) {
		GridLayoutSenninha s = new GridLayoutSenninha();
		s.setVisible(true);
	}

}

class MyButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4110690196514835914L;
	private boolean isBlock;

	public boolean isBlock() {
		return isBlock;
	}

	public void setBlock(boolean isBlock) {
		this.isBlock = isBlock;
	}
}
