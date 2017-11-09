package cn.senninha.sserver.lang.codec.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import cn.senninha.sserver.lang.codec.ClassType;
import cn.senninha.sserver.lang.codec.Codec;

/**
 * String的解码类，注意，编码的规则是：
 *  ----int(length)----byte[]----
 *  先一个int型的字节长度，不包括自身，然后是String的bytes(utf-8编码)
 * @author senninha on 2017年11月6日
 *
 */
@ClassType(clazz = String.class)
public class StringCodec implements Codec {

	@Override
	public void decode(ByteBuffer buf, Object m, Field f) {
		f.setAccessible(true);
		int length = buf.getInt();
		byte[] dst = new byte[length];
		buf.get(dst);
		try {
			String tem = new String(dst, 0, length, "utf-8");
			f.set(m, tem);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
			String tem = (String) f.get(m);
			byte[] src = tem.getBytes("utf-8");
			buf.putInt(src.length);
			buf.put(src);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
