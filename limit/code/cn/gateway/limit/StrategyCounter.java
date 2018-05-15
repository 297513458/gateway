package cn.gateway.limit;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StrategyCounter {

	class ObjectEntry {
		AtomicInteger errorCount;
		AtomicInteger successCount;
		AtomicInteger totalCount;

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("t:").append(successCount.get()).append(" f:").append(errorCount.get()).toString();
			return sb.toString();
		}

		public AtomicInteger getErrorCount() {
			return errorCount;
		}

		public void setErrorCount(AtomicInteger errorCount) {
			this.errorCount = errorCount;
		}

		public AtomicInteger getSuccessCount() {
			return successCount;
		}

		public void setSuccessCount(AtomicInteger successCount) {
			this.successCount = successCount;
		}

		public AtomicInteger getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(AtomicInteger totalCount) {
			this.totalCount = totalCount;
		}
	}

	private ObjectEntry[] counter;
	private ObjectEntry total;
	private int size;
	private volatile int head;
	private Lock lock = new ReentrantLock();
	private volatile long lastSlideTime;
	private volatile int span;
	private volatile int lastIndex;

	private void init(int second, int size) {
		if (second < 100)
			second = 100;
		this.span = (second * 1000) / size;
		this.size = size;
		if (size <= 1)
			size += 1;
		this.counter = new ObjectEntry[size];
		for (int i = 0; i < size; i++) {
			ObjectEntry o = new ObjectEntry();
			o.setSuccessCount(new AtomicInteger(0));
			o.setErrorCount(new AtomicInteger(0));
			counter[i] = o;
		}
		this.head = 0;
		total = new ObjectEntry();
		total.setTotalCount(new AtomicInteger(0));
		total.setSuccessCount(new AtomicInteger(0));
		total.setErrorCount(new AtomicInteger(0));
	}

	public StrategyCounter() {
		init(100, 5);
	}

	public StrategyCounter(int second) {
		init(second, 5);
	}

	public StrategyCounter(int second, int size) {
		init(second, size);
	}

	public void increment(boolean type) {
		if (System.currentTimeMillis() - lastSlideTime > span) {
			slide();
		}
		if (type)
			counter[head].getSuccessCount().incrementAndGet();
		else
			counter[head].getErrorCount().incrementAndGet();
	}

	public ObjectEntry get() {
		return counter[head];
	}

	private void slide() {
		if (!lock.tryLock())
			return;
		try {
			if (System.currentTimeMillis() - lastSlideTime <= span) {
				return;
			}
			int tail = (head + 1) % size;
			if (tail != lastIndex) {
				lastSlideTime = System.currentTimeMillis();
				lastIndex = tail;
			}
			head = tail;
			counter[head].getSuccessCount().set(0);
			counter[head].getErrorCount().set(0);
		} finally {
			lock.unlock();
		}
	}

	public void clean() {
		for (ObjectEntry os : counter) {
			os.getErrorCount().set(0);
			os.getSuccessCount().set(0);
		}
	}

	public void batchincrement(int error, int success) {
		if (System.currentTimeMillis() - lastSlideTime > span) {
			slide();
		}
		counter[head].getSuccessCount().addAndGet(success);
		counter[head].getErrorCount().addAndGet(error);
	}

	public ObjectEntry getCount() {
		total.getErrorCount().set(0);
		total.getSuccessCount().set(0);
		total.getTotalCount().set(0);
		for (ObjectEntry os : counter) {
			total.getErrorCount().addAndGet(os.getErrorCount().get());
			total.getSuccessCount().addAndGet(os.getSuccessCount().get());
		}
		total.getTotalCount().addAndGet(total.getSuccessCount().get());
		total.getTotalCount().addAndGet(total.getErrorCount().get());
		return total;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()).append(Thread.currentThread().getName()).append(":\t")
				.append(Arrays.toString(counter)).toString();
		return sb.toString();
	}

}