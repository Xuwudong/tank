package cn.senninha.sserver.lang.codec;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;


/**
 * 编码器，实现这个接口并编解码
 * @author senninha on 2017年11月5日
 *
 */
public interface Codec {
	public void decode(ByteBuffer buf, Object m, Field f);
	public void encode(ByteBuffer buf, Object m, Field f);
}
