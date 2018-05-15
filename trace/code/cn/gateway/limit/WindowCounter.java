package cn.gateway.limit;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowCounter {
	private Logger log = LoggerFactory.getLogger(WindowCounter.class);
	private AtomicInteger[] counter;
	private int count;
	private int second;
	private int size = 8;
	private volatile int head;
	private volatile long lastSlideTime;
	private volatile int span;
	private volatile int lastIndex;
	private volatile WindowStatus status = WindowStatus.RATE_FULL;
	private int maxSpeed;
	private int maxCount = 1000000;
	private int maxSecond = 800;
	private int maxChangeSpan = 10 * 1000;
	private String commandName;
	private Integer initCount;
	private Integer initSecond;
	private volatile long lastResetTime;
	private Lock lock = new ReentrantLock();

	private void init(int second, int count) {
		if (!lock.tryLock())
			return;
		try {
			if (second < 1)
				second = 1;
			if (second > maxSecond)
				second = maxSecond;
			if (initSecond != null && initSecond > second) {
				second = initSecond;
			}
			if (count < 1)
				count = 1;
			if (count > maxCount)
				count = maxCount;
			if (initCount != null && initCount < count) {
				count = initCount;
			}
			this.span = (second * 1000) / size;
			this.maxSpeed = count / size;
			if (this.counter == null) {
				this.counter = new AtomicInteger[size];
				for (int i = 0; i < this.size; i++) {
					counter[i] = new AtomicInteger(0);
				}
			}
			this.head = 0;
			if (log.isInfoEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append(this.getCommandName() != null ? this.getCommandName() : "");
				sb.append(":");
				sb.append(this.hashCode());
				sb.append("\t");
				if (this.count != 0) {
					sb.append(this.count).append("/").append(this.second).append(">>>");
				} else {
					sb.append("init>>>");
				}
				sb.append(count).append("/").append(second).toString();
				log.info(sb.toString());
			}
			if (initCount == null)
				initCount = count;
			if (initSecond == null)
				initSecond = second;
			this.second = second;
			this.count = count;
			if (initCount == count && initSecond == second) {
				status = WindowStatus.RATE_FULL;
			} else {
				if (this.count <= 1 && this.count < this.initCount) {
					if (second >= maxSecond) {
						status = WindowStatus.PROBE_MIN;
					} else {
						status = WindowStatus.PROBE;
					}
				} else {
					status = WindowStatus.RATE_NORMAL;
				}
			}
			lastResetTime = System.currentTimeMillis();
		} finally {
			lock.unlock();
		}
	}

	public WindowCounter(int second, int count, String commandName) {
		this.commandName = commandName;
		init(1, count);
	}

	public WindowCounter(int count) {
		init(1, count);
	}

	public WindowCounter(int second, int count) {
		init(second, count);
	}

	private boolean increment() {
		if (System.currentTimeMillis() - lastSlideTime > span) {
			slide();
		}
		if (maxSpeed > 0 && get() < maxSpeed) {
			getAndInc();
			return true;
		} else if (maxSpeed <= 0 && getTot() < this.count) {
			getAndInc();
			return true;
		} else {
			return false;
		}
	}

	private void slide() {
		if (!lock.tryLock())
			return;
		try {
			int tail = (head + 1) % size;
			if (tail != lastIndex) {
				lastSlideTime = System.currentTimeMillis();
				lastIndex = tail;
			}
			head = tail;
			set();
		} finally {
			lock.unlock();
		}
	}

	protected int getCount() {
		return count;
	}

	private void set() {
		try {
			counter[head].set(0);
		} catch (Exception e) {
		}
	}

	private int getTot() {
		try {
			int count = 0;
			for (AtomicInteger a : counter) {
				count += a.get();
			}
			return count;
		} catch (Exception e) {
		}
		return 0;
	}

	private int get() {
		try {
			return counter[head].get();
		} catch (Exception e) {
		}
		return 0;
	}

	protected int getAndInc() {
		try {
			return counter[head].getAndIncrement();
		} catch (Exception e) {
			return 0;
		}
	}

	protected boolean enable() {
		try {
			return increment();
		} catch (Exception e) {
			return false;
		}
	}

	protected int getSecond() {
		return second;
	}

	public boolean reset(int rate) {
		if ((System.currentTimeMillis() - lastResetTime) < maxChangeSpan) {
			return false;
		}
		if (!lock.tryLock())
			return false;
		try {
			if ((System.currentTimeMillis() - lastResetTime) < maxChangeSpan) {
				return false;
			}
			int second = this.getSecond();
			int count = this.getCount();
			if (rate < 0)
				rate = 10;
			if (rate < 100) {
				if (this.getCount() <= 1) {
					second = this.getSecond() * 10;
				} else {
					count = (this.getCount() * rate / 100);
				}
			} else if (rate > 100) {
				if (this.getCount() <= 1 && this.getSecond() > this.initSecond) {
					second = this.getSecond() / 10;
				} else if (this.getCount() < this.initCount) {
					count = (int) Math.ceil((this.getCount() * rate / 100.00));
				}
			} else
				return false;
			if (this.changed(second, count)) {
				init(second, count);
				return true;
			}
		} finally {
			lock.unlock();
		}
		return false;
	}

	private boolean changed(int second, int count) {
		boolean fage = true;
		if (this.initSecond != null && this.initSecond > second) {
			second = this.initSecond;
		}
		if (this.initCount != null && this.initCount < count) {
			count = this.initCount;
		}
		if (second == this.getSecond() && count == this.getCount()) {
			fage = false;
		}
		return fage;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("slide>").append(Thread.currentThread().getName()).append(":\t")
				.append(Arrays.toString(counter)).toString();
	}

	public WindowStatus getStatus() {
		return status;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

}