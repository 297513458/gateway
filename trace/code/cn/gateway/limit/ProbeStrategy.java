package cn.gateway.limit;

public class ProbeStrategy implements Strategy {
	public StrategyType type = StrategyType.PROBE;
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
		} else if (command.strategyCount.getCount().errorCount.get() > thresholdCount)
			change = shrink(command);
		else
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
		if (command == null)
			return false;
		if (command.getCounter().getStatus().ordinal() != WindowStatus.PROBE.ordinal()) {
			return false;
		}
		if (!command.lock.tryLock())
			return false;
		try {
			if (command.getCounter().getStatus().ordinal() == WindowStatus.PROBE.ordinal()) {
				if (command.strategyCount.getCount().successCount.get() == 0
						&& command.strategyCount.getCount().errorCount.get() > thresholdCount
						&& System.currentTimeMillis() - command.changeTime > 10 * 1000) {
					command.currentCap = 0;
					return true;
				}
			}
		} finally {
			command.lock.unlock();
		}
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
		if (command == null)
			return;
		command.strategyCount.increment(code);
		if (code) {
			command.strategyCount.get().errorCount.set(0);
		} else {
			command.strategyCount.get().successCount.set(0);
		}
		change(command);
	}

	public StrategyType getType() {
		return type;
	}
}