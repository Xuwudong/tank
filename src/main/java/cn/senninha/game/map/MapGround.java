package cn.senninha.game.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.senninha.sserver.client.Client;

public class MapGround {
	private long mapId;
	private List<Grid> blocks;
	private Map<Integer, Client> clientInMap;
	
	public MapGround(int mapId, List<Grid> blocks) {
		super();
		this.mapId = mapId;
		this.blocks = blocks;
		clientInMap = new HashMap<Integer, Client>();
	}
	
	public long getMapId() {
		return mapId;
	}
	public void setMapId(long mapId) {
		this.mapId = mapId;
	}
	public List<Grid> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<Grid> blocks) {
		this.blocks = blocks;
	}
	public Map<Integer, Client> getClientInMap() {
		return clientInMap;
	}
	public void setClientInMap(Map<Integer, Client> clientInMap) {
		this.clientInMap = clientInMap;
	}
	
	
}
