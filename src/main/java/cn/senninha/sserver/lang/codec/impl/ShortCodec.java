package cn.senninha.sserver.lang.codec.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import cn.senninha.sserver.lang.codec.ClassType;
import cn.senninha.sserver.lang.codec.Codec;

/**
 * short的编解码，注意，不可以解码{@link Short}封装类
 * @author senninha on 2017年11月6日
 *
 */
@ClassType(clazz = short.class)
public class ShortCodec implements Codec {

	@Override
	public void decode(ByteBuffer buf, Object m, Field f) {
		f.setAccessible(true);
		try {
			f.set(m, buf.getShort());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void encode(ByteBuffer buf, Object m, Field f) {
		f.setAccessible(true);
		try {
			buf.putShort(f.getShort(m));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
