package cn.senninha.sserver.lang;

import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 将
 * @author senninha on 2017年11月8日
 *
 */
public class ByteBufUtil {
	/**
	 *  {@link ByteBuf} 转化成 {@link ByteBuffer}
	 * @param buf
	 * @return
	 */
	public static ByteBuffer convert(ByteBuf buf){
		ByteBuffer buffer = ByteBuffer.allocate(buf.readableBytes());
		byte[] src = new byte[buf.readableBytes()];
		buf.getBytes(0, src);
		buffer.put(src);
		buffer.flip();
		return buffer;
	}
	
	/**
	 * {@link ByteBuffer} 转化成 {@link ByteBuf}
	 * @param buf
	 * @return
	 */
	public static ByteBuf convert(ByteBuffer buffer){
		byte[] src = buffer.array();
		ByteBuf buf = Unpooled.copiedBuffer(src);
		return buf;
	}
}
