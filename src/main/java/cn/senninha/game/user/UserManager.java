package cn.senninha.game.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.game.user.message.ReqLoginMessage;
import cn.senninha.game.user.message.ResLoginMessage;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;

public class UserManager {
	private static UserManager manager;
	private Logger logger = LoggerFactory.getLogger(UserManager.class);

	private UserManager() {

	}

	public static UserManager getInstance() {
		if (manager == null) {
			synchronized (UserManager.class) {
				if (manager == null) {
					manager = new UserManager();
				}
			}
		}
		return manager;
	}
	
	public void login(int sessionId, ReqLoginMessage m) {
		Client client = ClientContainer.getInstance().getClient(sessionId);
		
		
		//假装登陆成功
		client.setSessionInCtx(sessionId);
		client.pushMessage(ResLoginMessage.valueOf((byte)1));
		
		logger.error("登陆成功:{}", m.getUsername());
	}
}
