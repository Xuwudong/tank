package cn.senninha.sserver;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.sserver.lang.dispatch.HandlerFactory;
import cn.senninha.sserver.message.HelloMessage;

public class Test {

	public static void main(String[] args) {
		loggerTest();
	}
	
	public static void loggerTest() {
		Logger logger = LoggerFactory.getLogger(Test.class);
		logger.debug("senninha" + new Date());
	}
	
	public void dispatchTest() {
		HelloMessage hm = new HelloMessage();
		hm.setA(10);
		hm.setB(1111);
		hm.setCmd(12580);
		
		HandlerFactory hf = HandlerFactory.getInstance();
		hf.dispatch(hm, 1);
	}

}
