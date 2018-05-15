package cn.gateway.limit;

public class StrategyFactory {
	private static Strategy f = new FullStrategy();
	private static Strategy rate = new RateStrategy();
	private static Strategy p = new ProbeStrategy();
	private static Strategy m = new MinStrategy();

	public static Strategy get(int type) {
		if (type == WindowStatus.RATE_FULL.ordinal()) {
			return f;
		} else if (type == WindowStatus.PROBE.ordinal()) {
			return p;
		} else if (type == WindowStatus.PROBE_MIN.ordinal()) {
			return m;
		} else
			return rate;
	}
}