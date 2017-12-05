package cn.senninha.game.map.util;

import cn.senninha.game.map.Grid;

public class AStarNode {
	private AStarNode before;
	private Grid value;
	private AStarNode next;

	public AStarNode(Grid value, double gDistance) {
		super();
		this.value = value;
		this.gDistance = gDistance;
	}
	

	public AStarNode() {
		super();
	}


	public AStarNode getBefore() {
		return before;
	}

	public void setBefore(AStarNode before) {
		this.before = before;
	}

	public Grid getValue() {
		return value;
	}

	public void setValue(Grid value) {
		this.value = value;
	}

	public AStarNode getNext() {
		return next;
	}

	public void setNext(AStarNode next) {
		this.next = next;
	}

	public double getgDistance() {
		return gDistance;
	}

	public void setgDistance(double gDistance) {
		this.gDistance = gDistance;
	}

	/** 当前节点到上一个节点的距离 **/
	private double gDistance;

	@Override
	public String toString() {
		String rValue =  "{" + value.getX() + "," + value.getY() + "}";
		if(next != null){
			rValue = rValue + next.toString();
		}
		return rValue;
	}

}
