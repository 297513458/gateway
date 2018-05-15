package cn.gateway.limit;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LimitCommand {
	private int count;
	private int second;
	private String commandName;
	//
	private WindowCounter counter;
	private Strategy strategy;
	private Strategy laststrategy;
	//
	public volatile int currentCap = 100;
	public volatile long changeTime;
	//
	public Lock lock = new ReentrantLock();
	public StrategyCounter strategyCount = new StrategyCounter();

	public LimitCommand() {
		this(null);
	}

	public LimitCommand(String commandName) {
		this(100, commandName);
	}

	public LimitCommand(int count, String commandName) {
		this(1, count, commandName);
	}

	public LimitCommand(int second, int count, String commandName) {
		this.counter = new WindowCounter(second, count,commandName);
		this.counter.setCommandName(commandName);
		this.setCommandName(commandName);
		this.setCount(count);
		this.setSecond(second);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public WindowCounter getCounter() {
		return counter;
	}

	public void setCounter(WindowCounter counter) {
		this.counter = counter;
	}

	public StrategyCounter getStrategyCount() {
		return strategyCount;
	}

	public void setStrategyCount(StrategyCounter strategyCount) {
		this.strategyCount = strategyCount;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public Strategy getLaststrategy() {
		return laststrategy;
	}

	public void setLaststrategy(Strategy laststrategy) {
		this.laststrategy = laststrategy;
	}

	public int getCurrentCap() {
		return currentCap;
	}

	public void setCurrentCap(int currentCap) {
		this.currentCap = currentCap;
	}

	public long getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(long changeTime) {
		this.changeTime = changeTime;
	}
}