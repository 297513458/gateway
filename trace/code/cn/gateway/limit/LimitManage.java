package cn.gateway.limit;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LimitManage {
	private static Lock lock = new ReentrantLock();
	private static Map<String, LimitCommand> map = new WeakHashMap<>();

	private static Map<String, Limit> limInfo = null;
	private static Lock loadlock = new ReentrantLock();

	private static Lock eslock = new ReentrantLock();
	private static ExecutorService es = null;

	protected static Strategy strategy(String command) {
		LimitCommand lineCommand = getLimitCommand(command);
		if (lineCommand == null) {
			return null;
		}
		lineCommand.setStrategy(StrategyFactory.get(lineCommand.getCounter().getStatus().ordinal()));
		if (lineCommand.getStrategy() != null && !lineCommand.getStrategy().equals(lineCommand.getLaststrategy())) {
			lineCommand.getStrategy().clean(lineCommand);
			lineCommand.setLaststrategy(lineCommand.getStrategy());
		}
		return lineCommand.getStrategy();
	}

	protected static void notify(String command, boolean code) {
		if (command == null || command.trim().length() == 0)
			return;
		if (es == null) {
			eslock.lock();
			try {
				if (es == null) {
					es = Executors.newFixedThreadPool(40);
				}
			} finally {
				eslock.unlock();
			}
		}
		es.submit(new Runnable() {
			@Override
			public void run() {
				asycNotify(command, code);
			}
		});
	}

	protected static LimitCommand getLimitCommand(String command) {
		if (command == null || command.trim().length() == 0)
			return null;
		LimitCommand comd = map.get(command);
		if (comd == null) {
			lock.lock();
			try {
				if (comd == null) {
					Limit lim = load(command);
					if (lim == null)
						lim = new Limit();
					if (lim.getCount() <= 0)
						lim.setCount(100);
					if (lim.getSecond() <= 0)
						lim.setSecond(1);
					lim.setCommandName(command);
					comd = new LimitCommand(lim.getSecond(), lim.getCount(), command);
					map.put(command, comd);
				}
			} finally {
				lock.unlock();
			}
		}
		return comd;
	}

	private static Limit load(String command) {
		if (command == null || command.trim().length() == 0)
			return null;
		if (limInfo == null) {
			Scanner scan = null;
			loadlock.lock();
			try {
				if (limInfo == null) {
					limInfo = new HashMap<>();
					scan = new Scanner(ClassLoader.getSystemResourceAsStream("lim.properties"));
					while (scan.hasNext()) {
						try {
							String t = scan.nextLine().trim();
							if (t.startsWith("#") || t.startsWith("/"))
								continue;
							String[] st = t.split(" ");
							if (st.length == 0)
								continue;
							Limit lim = new Limit();
							lim.setCommandName(st[0]);
							if (st.length >= 2) {
								String[] rate = st[1].split("/");
								try {
									lim.setCount(Integer.parseInt(rate[0]));
								} catch (Exception e) {
								}
								try {
									lim.setSecond(Integer.parseInt(rate[1]));
								} catch (Exception e) {
								}
							}
							limInfo.put(lim.getCommandName(), lim);
						} catch (Exception e) {
						}
					}
				}
			} finally {
				if (scan != null)
					scan.close();
				loadlock.unlock();
			}
		}
		if (limInfo == null)
			return null;
		Limit lim = limInfo.get(command);
		if (lim == null)
			lim = limInfo.get("*");
		return lim;
	}

	protected static boolean enable(String command) {
		LimitCommand lineCommand = getLimitCommand(command);
		if (lineCommand == null)
			return true;
		return lineCommand.getCounter().enable();
	}

	public WindowCounter getCounter(String command) {
		LimitCommand lineCommand = getLimitCommand(command);
		return lineCommand.getCounter();
	}

	private static void asycNotify(String command, boolean code) {
		if (command == null || command.trim().length() == 0)
			return;
		Strategy st = strategy(command);
		if (st == null)
			return;
		LimitCommand lineCommand = getLimitCommand(command);
		st.submit(lineCommand, code);
	}
}