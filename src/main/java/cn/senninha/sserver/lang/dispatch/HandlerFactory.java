package cn.senninha.sserver.lang.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import cn.senninha.sserver.bhandler.HelloHandler;
import cn.senninha.sserver.lang.ClassFilter;
import cn.senninha.sserver.lang.ClassUtil;
import cn.senninha.sserver.lang.message.BaseMessage;

/**
 * 缓存业务handler的容器,单例子获取的时候会自动扫描获取所有的业务Handler({@link HelloHandler}
 * 
 * @author senninha on 2017年11月8日
 *
 */
public class HandlerFactory {
	private Map<Integer, MessageInvokeWrapper> invokeContainer;

	private static HandlerFactory instance;

	private HandlerFactory() {
		init();
	}

	/**
	 * 仅可以被构造函数调用
	 */
	private void init() {
		invokeContainer = new HashMap<>(); // 初始化缓存容器

		ClassUtil.scanPackage("cn", false, new ClassFilter<Object>() {

			@Override
			public boolean filter(Class<Object> clazz) {
				Annotation hAnnotation = clazz.getAnnotation(MessageHandler.class);
				if (hAnnotation != null) {
					Object obj = null;
					try {
						obj = clazz.newInstance();
						Method[] array = clazz.getDeclaredMethods();
						for (Method m : array) {
							MessageInvoke iAnnotation = m.getAnnotation(MessageInvoke.class);
							if (iAnnotation != null) {
								int cmd = iAnnotation.cmd();
								Parameter[] params = m.getParameters();
								if (!(params[0].getType() == int.class)
										|| !(BaseMessage.class.isAssignableFrom(params[1].getType()))) {
									System.err.println("业务Handler函数参数必须遵循(int,BaseMessage)的格式");
								} else {
									MessageInvokeWrapper miw = new MessageInvokeWrapper(obj, m);
									invokeContainer.put(cmd, miw);
								}
							}
						}
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

				}
				return false;
			}
		});
	}

	/**
	 * 根据message自动分发到对应的方法处理
	 * 
	 * @param message
	 * @param sessionId
	 */
	public void dispatch(BaseMessage message, int sessionId) {
		int cmd = message.getCmd();
		MessageInvokeWrapper miw = invokeContainer.get(cmd);
		if (miw != null) {
			Object obj = miw.getObj();
			Method m = miw.getMethod();
			try {
				m.invoke(obj, sessionId, message);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}else{
			System.err.println("没有该通信协议的业务Handler:" + message.toString());
		}
	}

	public static HandlerFactory getInstance() {
		if (instance == null) {
			synchronized (HandlerFactory.class) {
				if (instance == null) {
					instance = new HandlerFactory();
				}
			}
		}
		return instance;
	}
}
