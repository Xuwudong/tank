package cn.senninha.sserver.lang.dispatch;

import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import cn.senninha.game.GameStatus;
import cn.senninha.game.ai.AiManager;
import cn.senninha.game.map.manager.MapManager;
import cn.senninha.sserver.ServerStart;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
import cn.senninha.sserver.lang.message.BaseMessage;

public class HandleContext {
	private static HandleContext context;
	private Processor[] processor;
	
	private HandleContext() {
		init();
	}
	
	/**
	 * 初始化场景线程
	 */
	private void init() {
		processor = new Processor[1];
		processor[0] = new Processor("handle-thread-0");
		processor[0].start();
		
		/** 初始化行走检测任务 **/
		addCommand(0, new Task(GameStatus.GAME_RUN_CHECK_INTERVEL.getValue(), true, -1, TimeUnit.MILLISECONDS, new Runnable() {
			
			@Override
			public void run() {
				MapManager.getInstance().run();
			}
		}));
		
		/** 注册子弹检测任务 **/
		addCommand(0, new Task(GameStatus.GAME_BULLETS_CHECK_INTERVEL.getValue(), true, -1, TimeUnit.MILLISECONDS, new Runnable() {
			
			@Override
			public void run() {
				MapManager.getInstance().checkBullets();
			}
		}));
		
		/** 注册AI检测任务 **/
		addCommand(0, new Task(GameStatus.GAME_AI_CHECK_INTERVAL.getValue(), true, -1, TimeUnit.MILLISECONDS, new Runnable() {
			
			@Override
			public void run() {
				AiManager.getInstance().check();
			}
		}));
	}
	
	public void dispatch(int sessionId, BaseMessage message) {
		Client client = ClientContainer.getInstance().getClient(sessionId);
		if(client != null) {
			int line = client.getLine();
			if(line == -1) {
				line = sessionId % processor.length;
				client.setLine(line);
			}
			processor[line].addCommand(new Task(0, false, 0, TimeUnit.MILLISECONDS, new Runnable() {
				public void run() {
					HandlerFactory.getInstance().dispatch(message, sessionId);
				}
			}));
		}
	}
	
	/**
	 * 添加任务到HandleContext
	 * @param task
	 */
	public void addCommand(int line, Task task) {
		processor[line].addCommand(task);
	}
	
	public static HandleContext getInstance() {
		if(context == null) {
			synchronized (HandleContext.class) {
				if(context == null) {
					context = new HandleContext();
				}
			}
		}
		return context;
	}
	public static void main(String[] args) {
		Processor p = new Processor("senninha");
		p.addCommand(new Task(1000, true, 10, TimeUnit.MILLISECONDS, new Runnable() {

			@Override
			public void run() {
				System.out.println(new Date().toString());
			}
		}));
		p.start();
	}
}

class Processor extends Thread {
	private DelayQueue<Task> queue;

	Processor(String name) {
		super(name);
		queue = new DelayQueue<>();
	}

	public void addCommand(Task task) {
		queue.add(task);
	}

	@Override
	public void run() {
		while (ServerStart.SERVER_RUNNING.get()) {
			try {
				Task task = queue.take();
				Runnable r = task.getRunnable();
				if (r != null) {
					try {
						r.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (task.isNeedRepeat() && task.getRepeatTime() != 1) {
						task.correctTime(); // 修正执行时间
						addCommand(task);
					}
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

