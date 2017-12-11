- 项目整体结构
```
	---网关(netty)
	---逻辑处理(HandleContext)
```

- 网关
	- netty
	 使用**netty**框架进行I/O读写
	 
	- 协议
	  协议格式如下:
	  
	  |包头|包长度|cmd|内容|
	  |:---:|:---:|:---:|:---:|:---:|
	  |0x68|int|int|xxx|
	  
	  拆包使用的netty带的工具**LengthFieldBasedFrameDecoder**，指定构造参数，即可拆出完整的一个包。
	  
	- 二进制数据--->Java Object
	  参考项目里的实现，在项目启动的时候扫描并缓存对应协议实体类，缓存对应的编解码方法，然后根据**cmd**来获取缓存并进行编解码。
	  基础编解码支持**int,short,long,string,byte**，并支持包含这些基础编解码工具的**封装类**，也支持**List**。
	  
	 - 分发处理
	 通过协议里的cmd，通过反射获取对应的执行方法，然后分发给**逻辑处理线程处理**
	也就是说，netty的**work**线程不负责处理逻辑，只是把协议包分发到对应的处理线程就完事，netty的线程只会处理**心跳业务**，方便debug挂起线程而不会让双端都掉线
	
	
	
	
	
	
- HandleContext
> HandleContext是处理处理分发所有业务逻辑的。
	- Processor
	Processor继承自**Thread**，持有一个**DelayQueue**队列来缓存需要处理的任务。对外提供添加任务的接口，支持如下类型任务：
		- 单/多次有/无延迟任务
		- 定时任务
	
	  
	  
	  
	  List<Grid> grids;
	  
	  	
