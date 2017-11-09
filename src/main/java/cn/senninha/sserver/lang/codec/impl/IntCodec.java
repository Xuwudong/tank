package cn.senninha.sserver.lang.codec.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import cn.senninha.sserver.lang.codec.ClassType;
import cn.senninha.sserver.lang.codec.Codec;

/**
 * int的编解码
 * @author senninha on 2017年11月6日
 *
 */
@ClassType(clazz = int.class)
public class IntCodec implements Codec {

	@Override
	public void decode(ByteBuffer buf, Object m, Field f) {
		f.setAccessible(true);
		try {
			f.setInt(m, buf.getInt());
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
			buf.putInt(f.getInt(m));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
