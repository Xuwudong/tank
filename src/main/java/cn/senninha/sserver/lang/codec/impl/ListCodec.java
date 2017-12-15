package cn.senninha.sserver.lang.codec.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import cn.senninha.sserver.lang.codec.ClassType;
import cn.senninha.sserver.lang.codec.Codec;
import cn.senninha.sserver.lang.codec.CodecFactory;
import cn.senninha.sserver.lang.codec.CodecFactory.MessageWrapper;
import cn.senninha.sserver.lang.message.MessageWrapperAnnotation;

@ClassType(clazz = List.class)
public class ListCodec implements Codec {

	@Override
	public void decode(ByteBuffer buf, Object m, Field f) {
		try {
			f.setAccessible(true);
			/** 读出cmd和本list长度 **/
			int cmd = buf.getInt();
			int size = buf.getInt();
			MessageWrapper mw = CodecFactory.getInstance().getMessageWrapper(cmd);
			Class<?> clazz = mw.clazz;
			List<Object> list = new ArrayList<>(size);
			f.set(m, list);
			
			// 解码对应的值并写入list里
			for (int j = 0; j < size; j++) {
				Object e = clazz.newInstance(); // new出list里的对象
				for (int i = 0; i < mw.fields.length; i++) {
					mw.list.get(i).decode(buf, e, mw.fields[i]);
				}
				list.add(e);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void encode(ByteBuffer buf, Object m, Field f) {
		try {
			f.setAccessible(true);
			List list = (List) f.get(m);
			MessageWrapper mw = CodecFactory.getInstance()
					.getMessageWrapper(list.get(0).getClass().getAnnotation(MessageWrapperAnnotation.class).cmd());
			buf.putInt(mw.cmd);		//写入cmd
			buf.putInt(list.size()); //写入size
			for(Object obj : list) {
				for(int i = 0 ; i < mw.fields.length ; i++) {
					mw.list.get(i).encode(buf, obj, mw.fields[i]);
				}
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
