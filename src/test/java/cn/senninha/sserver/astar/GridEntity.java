package cn.senninha.sserver.astar;

import java.awt.Color;

import javax.swing.JButton;

import cn.senninha.game.map.Grid;


public class GridEntity extends Grid {
	private JButton but;
	private Color color;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if(this.color != null && this.color.equals(color)){
			this.color = Color.white;
			this.setStatus((byte)0);
			but.setBackground(this.color);
			return;
		}
		this.color = color;
		if(color == Color.red){
			this.setStatus((byte)2);
		}else{
			this.setStatus((byte)0);
		}
		but.setBackground(color);
	}

	public JButton getBut() {
		return but;
	}

	public void setBut(JButton but) {
		this.but = but;
	}
}
