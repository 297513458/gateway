package cn.gateway.limit;

public class MinStrategy implements Strategy {
	public StrategyType type = StrategyType.PROBE_MIN;
	private static final int thresholdCount = 20;

	public boolean change(LimitCommand command) {
		if (command == null)
			return false;
		boolean change = Boolean.FALSE;
		if (System.currentTimeMillis() - command.changeTime <= 10 * 1000) {
			return change;
		}
		if (command.strategyCount.getCount().successCount.get() > thresholdCount) {
			change = expand(command);
		} else
			return false;
		if (change && (System.currentTimeMillis() - command.changeTime > 10 * 1000)) {
			if (command.getCounter().reset(command.currentCap)) {
				command.changeTime = System.currentTimeMillis();
				command.strategyCount.clean();
			}
		}
		return change;
	}

	public boolean shrink(LimitCommand command) {
		return false;
	}

	public boolean expand(LimitCommand command) {
		if (command == null)
			return false;
		if (!command.lock.tryLock())
			return false;
		try {
			if (command.strategyCount.getCount().errorCount.get() == 0
					&& command.strategyCount.getCount().successCount.get() > thresholdCount
					&& System.currentTimeMillis() - command.changeTime > 10 * 1000) {
				command.currentCap = 130;
				return true;
			}
			return false;
		} finally {
			command.lock.unlock();
		}
	}

	/**
	 * 按状态提交信息
	 * 
	 * @param code
	 */
	public void submit(LimitCommand command, boolean code) {
		command.strategyCount.increment(code);
		if (code) {
			command.strategyCount.get().errorCount.set(0);
		} else {
			command.strategyCount.get().successCount.set(0);
		}
		this.change(command);
	}

	public StrategyType getType() {
		return type;
	}
}