package cn.senninha.sserver;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.senninha.sserver.handler.DispatchHandler;
import cn.senninha.sserver.handler.EncodeHandler;
import cn.senninha.sserver.lang.codec.CodecFactory;
import cn.senninha.sserver.lang.dispatch.HandleContext;
import cn.senninha.sserver.lang.dispatch.HandlerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 
 * @author senninha on 2017年11月7日
 *
 */
public class ServerStart {
	public static final AtomicBoolean SERVER_RUNNING = new AtomicBoolean(true);
	private final int port;
	private final int maxFrameLength; // 最大长度
	private final int lengthFieldOffset; // 偏移地址
	private final int lengthFieldLength; // 表示报文长度的字节数
	private final int lengthAdjustment; // 从何处开始计算包长度，默认是从报文长度的下一个字节开始
	private final int initialBytesToStrip; // 裁减包头
	private final boolean failFast; // 快速失败。一旦到达maxFrameLength，马上抛出异常。
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private Logger logger = LoggerFactory.getLogger(ServerStart.class);

	public ServerStart(int port, int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, boolean failFast) {
		super();
		this.port = port;
		this.maxFrameLength = maxFrameLength;
		this.lengthFieldOffset = lengthFieldOffset;
		this.lengthFieldLength = lengthFieldLength;
		this.lengthAdjustment = lengthAdjustment;
		this.initialBytesToStrip = initialBytesToStrip;
		this.failFast = failFast;
	}

	public void run() throws Exception {
		init(); // 初始化系统
		bossGroup = new NioEventLoopGroup(1); // (1)
		workerGroup = new NioEventLoopGroup(1);
		try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class) // (3)
					.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast(new EncodeHandler());
							ch.pipeline()
									.addLast(new DispatchHandler(maxFrameLength,
											lengthFieldOffset,
											lengthFieldLength, lengthAdjustment,
											initialBytesToStrip, failFast));
							ch.pipeline().addLast(new IdleStateHandler(0, 0, 30));
						}
					}).option(ChannelOption.SO_BACKLOG, 128) // (5)
					.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(port).sync(); // (7)

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	private void init() {
		/** 初始化分发以及编解码工具 **/
		CodecFactory.getInstance();
		logger.error("初始化编解码工具成功");
		
		HandlerFactory.getInstance();
		logger.error("初始化分发工厂成功");
		
		HandleContext.getInstance();
		logger.error("初始化场景线程成功");
	}

	public static void main(String[] args) throws Exception {

		new ServerStart(9527, 1024 * 16, 1, 2, 0, 3, true).run();
	}
}