package cn.senninha.sserver.lang.codec.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import cn.senninha.sserver.lang.codec.ClassType;
import cn.senninha.sserver.lang.codec.Codec;

/**
 * long的编解码
 * @author senninha on 2017年11月6日
 *
 */
@ClassType(clazz = long.class)
public class LongCodec implements Codec{

	@Override
	public void decode(ByteBuffer buf, Object m, Field f) {
		f.setAccessible(true);
		try {
			f.setLong(m, buf.getLong());
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
			buf.putLong(f.getLong(m));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
