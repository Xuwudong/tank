package cn.senninha.sserver.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientContainer {
	private Map<Integer, Client> container;
	private static ClientContainer instance;
	
	public static ClientContainer getInstance(){
		if(instance == null){
			synchronized (ClientContainer.class) {
				if(instance == null){
					instance = new ClientContainer();
				}
			}
		}
		return instance;
	}
	
	private ClientContainer(){
		container = new ConcurrentHashMap<>();
	}
	
	public Client getClient(int sessionId){
		return container.get(sessionId);
	}
	
	public boolean addClient(Client client){
		container.put(client.getSessionId(), client);
		return true;
	}
	
	public boolean isOnline(int sessionId){
		return container.containsKey(sessionId);
	}
	
	public Client remove(int sessionId){
		return container.remove(sessionId);
	}
}
