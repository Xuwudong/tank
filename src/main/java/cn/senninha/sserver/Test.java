package cn.senninha.sserver;

import cn.senninha.sserver.lang.dispatch.HandlerFactory;
import cn.senninha.sserver.message.HelloMessage;

public class Test {

	public static void main(String[] args) {
		HelloMessage hm = new HelloMessage();
		hm.setA(10);
		hm.setB(1111);
		hm.setCmd(12580);
		
		HandlerFactory hf = HandlerFactory.getInstance();
		hf.dispatch(hm, 1);
	}

}
