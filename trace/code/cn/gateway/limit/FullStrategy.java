package cn.gateway.limit;

public class FullStrategy implements Strategy {
	private static final int thresholdCount = 100;
	private static final int thresholdShrinkRate = 20;
	public StrategyType type = StrategyType.RATE_FULL;

	public boolean change(LimitCommand command) {
		if (command == null)
			return false;
		boolean change = Boolean.FALSE;
		if (command.strategyCount.getCount().errorCount.get() > thresholdCount
				&& (command.strategyCount.getCount().errorCount.get() * 100
						/ (command.strategyCount.getCount().totalCount.get())) >= thresholdShrinkRate) {
			change = shrink(command);
		} else
			return false;
		if (change && (System.currentTimeMillis() - command.changeTime > 10 * 1000)) {
			command.changeTime = System.currentTimeMillis();
			System.err.println(command.currentCap);
			if (command.getCounter().reset(command.currentCap)) {
				clean(command);
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
					System.err.println(command.currentCap);
					return true;
				}
			}
		} finally {
			command.lock.unlock();
		}
		return false;
	}

	public boolean expand(LimitCommand command) {
		return false;
	}

	public StrategyType getType() {
		return type;
	}
}