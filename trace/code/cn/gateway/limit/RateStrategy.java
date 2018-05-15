package cn.gateway.limit;

public class RateStrategy implements Strategy {
	public StrategyType type = StrategyType.RATE;
	private static final int thresholdExpandRate = 98;
	private static final int thresholdCount = 100;
	private static final int thresholdShrinkRate = 20;

	public boolean change(LimitCommand command) {
		if (command == null)
			return false;
		boolean change = Boolean.FALSE;
		if (command.strategyCount.getCount().errorCount.get() > thresholdCount
				&& (command.strategyCount.getCount().errorCount.get() * 100
						/ (command.strategyCount.getCount().totalCount.get())) >= thresholdShrinkRate) {
			change = shrink(command);
		} else if (command.strategyCount.getCount().successCount.get() > thresholdCount
				&& (command.strategyCount.getCount().successCount.get() * 100
						/ (command.strategyCount.getCount().totalCount.get())) >= thresholdExpandRate) {
			change = expand(command);
		}
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
		if (!command.lock.tryLock())
			return false;
		try {
			int rate = command.strategyCount.getCount().errorCount.get() * 100
					/ (command.strategyCount.getCount().totalCount.get());
			if (command.strategyCount.getCount().errorCount.get() > thresholdCount && rate >= thresholdShrinkRate) {
				if (command.getCounter().getStatus().ordinal() < WindowStatus.PROBE.ordinal()
						&& System.currentTimeMillis() - command.changeTime > 10 * 1000) {
					int cap = 100 - rate;
					if (cap < 10)
						cap = 10;
					command.currentCap = cap;
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
			if (command.strategyCount.getCount().successCount.get() > thresholdCount
					&& (command.strategyCount.getCount().successCount.get() * 100
							/ (command.strategyCount.getCount().totalCount.get())) >= thresholdExpandRate) {
				if (command.getCounter().getStatus().ordinal() != WindowStatus.RATE_FULL.ordinal()
						&& System.currentTimeMillis() - command.changeTime > 10 * 1000) {
					command.currentCap = 130;
					return true;
				}
			}
		} finally {
			command.lock.unlock();
		}
		return false;
	}

	public StrategyType getType() {
		return type;
	}
}