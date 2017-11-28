package cn.senninha.sserver.lang.codec.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import cn.senninha.sserver.lang.codec.ClassType;
import cn.senninha.sserver.lang.codec.Codec;
import cn.senninha.sserver.lang.codec.CodecFactory;
import cn.senninha.sserver.lang.codec.CodecFactory.MessageWrapper;
import cn.senninha.sserver.lang.message.MessageWrapperAnnotation;

@ClassType(clazz = WrapperCodec.class)
public class WrapperCodec implements Codec {

	@Override
	public void decode(ByteBuffer buf, Object m, Field f) {
		try {
			MessageWrapper mw = CodecFactory.getInstance()
					.getMessageWrapper(f.getType().getAnnotation(MessageWrapperAnnotation.class).cmd());
			Object innerObj = mw.clazz.newInstance();
			for (int i = 0; i < mw.fields.length; i++) {
				mw.list.get(i).decode(buf, innerObj, mw.fields[i]);
			}
			f.setAccessible(true);
			f.set(m, innerObj);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void encode(ByteBuffer buf, Object m, Field f) {
		try {
			f.setAccessible(true);
			Class<?> clazz = f.getType();
			MessageWrapperAnnotation annotation = (MessageWrapperAnnotation) clazz
					.getAnnotation(MessageWrapperAnnotation.class);
			MessageWrapper mw = CodecFactory.getInstance().getMessageWrapper(annotation.cmd());
			for (int i = 0; i < mw.fields.length; i++) {
				mw.list.get(i).encode(buf, f.get(m), mw.fields[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
