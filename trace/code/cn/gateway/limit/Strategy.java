package cn.gateway.limit;

public interface Strategy {
	enum StrategyType {
		RATE_FULL, RATE, PROBE, PROBE_MIN;
	}

	public StrategyType type = StrategyType.RATE;

	public default StrategyType getType() {
		return type;
	}

	/**
	 * 按状态提交信息
	 * 
	 * @param code
	 */
	public default void submit(LimitCommand command, boolean code) {
		command.strategyCount.increment(code);
		this.change(command);
	}

	public default void clean(LimitCommand command) {
		command.strategyCount.clean();
	}

	public boolean change(LimitCommand command);

	/**
	 * 批量提交
	 * 
	 * @param error
	 * @param success
	 */
	public default void batch(LimitCommand command, int error, int success) {
		command.strategyCount.batchincrement(error, success);
	}

	public boolean shrink(LimitCommand command);

	public boolean expand(LimitCommand command);

}