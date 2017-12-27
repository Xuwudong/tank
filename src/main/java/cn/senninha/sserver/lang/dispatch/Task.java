package cn.senninha.sserver.lang.dispatch;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 场景任务
 * @author senninha
 *
 */
public class Task implements Delayed {
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
		this.executeTime = unit.convert(delay, TimeUnit.MILLISECONDS) + unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
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