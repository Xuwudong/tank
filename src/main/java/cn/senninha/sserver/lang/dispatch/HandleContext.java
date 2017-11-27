package cn.senninha.sserver.lang.dispatch;

import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import cn.senninha.sserver.ServerStart;
import cn.senninha.sserver.client.Client;
import cn.senninha.sserver.client.ClientContainer;
import cn.senninha.sserver.lang.message.BaseMessage;

public class HandleContext {
	private static HandleContext context;
	private Processor[] processor;
	
	private HandleContext() {
		processor = new Processor[1];
		processor[0] = new Processor("handle-thread-0");
		processor[0].start();
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
					r.run();
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

class Task implements Delayed {
	private long delay;
	private boolean needRepeat;
	private int repeatTime;
	private Runnable runnable;
	private long executeTime;
	private TimeUnit unit;

	/**
	 * 
	 * @param delay
	 *            延迟多少秒执行
	 * @param needRepeat
	 *            需要循环?
	 * @param repeatTime
	 *            循环次数，在needRepeat为1时生效, 若为-1，则表示无穷循环
	 * @param runnable
	 *            任务
	 */
	public Task(long delay, boolean needRepeat, int repeatTime, TimeUnit unit, Runnable runnable) {
		super();
		this.delay = delay;
		this.needRepeat = needRepeat;
		this.repeatTime = repeatTime;
		this.runnable = runnable;
		this.unit = unit;
	}

	public void correctTime() {
		this.executeTime = delay + unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		if (this.repeatTime != -1)
			this.repeatTime--;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public boolean isNeedRepeat() {
		return needRepeat;
	}

	public void setNeedRepeat(boolean needRepeat) {
		this.needRepeat = needRepeat;
	}

	public int isRepeatTime() {
		return repeatTime;
	}

	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	public long getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(long executeTime) {
		this.executeTime = executeTime;
	}

	public int getRepeatTime() {
		return repeatTime;
	}

	@Override
	public int compareTo(Delayed o) {
		// 大于返回1，小于返回-1,等于返回0
		return this.getDelay(TimeUnit.NANOSECONDS) > o.getDelay(TimeUnit.NANOSECONDS) ? 1
				: (this.getDelay(TimeUnit.NANOSECONDS) < o.getDelay(TimeUnit.NANOSECONDS) ? -1 : 0);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

}
