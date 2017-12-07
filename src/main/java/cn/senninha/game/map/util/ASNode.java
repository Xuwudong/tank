package cn.senninha.game.map.util;

import cn.senninha.game.map.Grid;

/**
 * A*搜寻结点
 * @author senninha
 *
 */
public class ASNode implements Comparable<ASNode> {
	private Grid value;
	private int gValue;
	private int hValue;
	private ASNode parent;

	public ASNode(Grid value, int gValue, int hValue, ASNode parent) {
		super();
		this.value = value;
		this.gValue = gValue;
		this.hValue = hValue;
		this.parent = parent;
	}

	public Grid getValue() {
		return value;
	}

	public void setValue(Grid value) {
		this.value = value;
	}

	public int getgValue() {
		return gValue;
	}

	public void setgValue(int gValue) {
		this.gValue = gValue;
	}

	public int gethValue() {
		return hValue;
	}

	public void sethValue(int hValue) {
		this.hValue = hValue;
	}

	public ASNode getParent() {
		return parent;
	}

	public void setParent(ASNode parent) {
		this.parent = parent;
	}

	@Override
	public int compareTo(ASNode o) {
		if (o.getgValue() + o.gethValue() > this.getgValue() + this.gethValue()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	/**
	 * 重写equal方法
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ASNode) {
			return this.value.equals(((ASNode) obj).getValue());
		}
		return false;
	}
	
	/**
	 * 重写hashCode方法
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public String toString() {
		String rValue = "{" + value.getX() + "," + value.getY() + "} ";
//		value.setStatus((byte)GridStatus.OTHER_TANK_DOWN.getStatus());
//		value.setPixelX(value.getX() * 40 + 20);
//		value.setPixelY(value.getY() * 40 + 20);
		if(parent != null) {
			rValue = rValue + parent.toString();
		}
		return rValue;
	}

}
