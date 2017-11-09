package cn.senninha.sserver.lang.codec;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.senninha.sserver.lang.ClassFilter;
import cn.senninha.sserver.lang.ClassUtil;
import cn.senninha.sserver.lang.message.BaseMessage;
import cn.senninha.sserver.lang.message.Message;

/**
 * 使用编解码工厂来自动化封装协议
 * @author senninha on 2017年11月5日
 *
 */
public class CodecFactory {
	private Map<Integer, MessageWrapper> messageMap = new HashMap<Integer, MessageWrapper>();
	@SuppressWarnings("rawtypes")
	private Map<Class, Codec> codecMap = new HashMap<Class, Codec>();

	private static CodecFactory codecFactory;

	public static void main(String[] args) {

	}

	public static CodecFactory getInstance() {
		if (codecFactory == null) {
			synchronized (CodecFactory.class) {
				if (codecFactory == null) {
					codecFactory = new CodecFactory();
				}
			}
		}
		return codecFactory;
	}
	
	private CodecFactory() {
		init();
	}

	private void init(){
		ClassUtil.scanPackage("cn.senninha", false, new ClassFilter<Message>() {

			@Override
			public boolean filter(Class<Message> clazz) {
				/** 扫描解码基础类 **/
				if(!clazz.isInterface() && Codec.class.isAssignableFrom(clazz)){
					try {
						Codec codec = (Codec) clazz.newInstance();
						ClassType cT = clazz.getAnnotation(ClassType.class);
						if(cT == null){
							throw new RuntimeException("Codec的实现类必须注明所解码的数据类型");
						}
						Class<?> codecClass = cT.clazz();
						codecMap.putIfAbsent(codecClass, codec);//缓存编解码工具
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					return false;
				}
				return false;
			}
			
		});
		
		
		ClassUtil.scanPackage("cn.senninha", false, new ClassFilter<Message>() {
			@Override
			public boolean filter(Class<Message> clazz) {
				
				/** 扫描具体通信协议**/
				Message m = clazz.getAnnotation(Message.class);
				System.out.println(clazz.toGenericString());
				if (m != null) {
					Integer protocol = m.cmd();
					Field[] fields = clazz.getDeclaredFields();
					List<Codec> list = new ArrayList<Codec>(fields.length);
					for(Field field : fields){
						Class<?> clazzType = field.getType();
						list.add(codecMap.get(clazzType));
					}
					MessageWrapper mw = new MessageWrapper();
					mw.clazz = clazz;
					mw.list = list;
					mw.fields = fields;
					messageMap.put(protocol, mw);
				}
				return false;
			}
		});
	}
	
	/**
	 *  解码，注意，此方法不会进行 flip ,请在调用之前使用{@link ByteBuffer} 的flip方法
	 * @param buf 输入ByteBuffer类
	 * @return
	 */
	public BaseMessage decode(ByteBuffer buf){
		int cmd = buf.getInt();
		MessageWrapper mw = messageMap.get(cmd);
		BaseMessage m = null;
		try {
			m = (BaseMessage) mw.clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		m.setCmd(cmd);
		
		for(int i = 0 ; i < mw.fields.length ; i++){
			Field field = mw.fields[i];
			Codec codec = mw.list.get(i);
			codec.decode(buf, m, field);
		}
		
		return m;
	}
	
	/**
	 * 
	 * @param message 必须实现 {@link Message} 注解，
	 * 注意，此方法不会进行 flip ,请在调用之后使用{@link ByteBuffer} 的flip方法再进行读操作
	 * @return
	 */
	public ByteBuffer encode(BaseMessage message){
		ByteBuffer buf = ByteBuffer.allocate(1024);
		int cmd = message.getCmd();
		buf.putInt(cmd);
		MessageWrapper mw = messageMap.get(cmd);
		
		for(int i = 0 ; i < mw.fields.length ; i++){
			mw.list.get(i).encode(buf, message, mw.fields[i]);
		}		
		return buf;
	}
	
	private class MessageWrapper{
		List<Codec> list;
		Field[] fields;
		Class<Message> clazz;
	}
}
