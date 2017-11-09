package cn.senninha.sserver.lang.codec.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import cn.senninha.sserver.lang.codec.ClassType;
import cn.senninha.sserver.lang.codec.Codec;

/**
 * byte的编解码
 * @author senninha on 2017年11月5日
 *
 */
@ClassType(clazz = byte.class)
public class ByteCodec implements Codec {
	
	public ByteCodec(){
		
	}

	@Override
	public void decode(ByteBuffer buf, Object m, Field f) {
		f.setAccessible(true);
		try {
			f.setByte(m, buf.get());
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
			Byte b = f.getByte(m);
			buf.put(b);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
